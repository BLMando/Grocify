package com.example.grocify.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.BuildConfig
import com.example.grocify.R
import com.example.grocify.data.remote.RetrofitObject
import com.example.grocify.states.MapUiState
import com.example.grocify.databinding.MapLayoutBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tomtom.sdk.common.measures.UnitSystem
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStore
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStoreConfiguration
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.mapmatched.MapMatchedLocationProvider
import com.tomtom.sdk.location.simulation.SimulationLocationProvider
import com.tomtom.sdk.location.simulation.strategy.InterpolationStrategy
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraChangeListener
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.camera.CameraTrackingMode
import com.tomtom.sdk.map.display.common.screen.Padding
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.map.display.ui.currentlocation.CurrentLocationButton
import com.tomtom.sdk.map.display.ui.logo.LogoView
import com.tomtom.sdk.navigation.ActiveRouteChangedListener
import com.tomtom.sdk.navigation.ProgressUpdatedListener
import com.tomtom.sdk.navigation.RoutePlan
import com.tomtom.sdk.navigation.TomTomNavigation
import com.tomtom.sdk.navigation.UnitSystemType
import com.tomtom.sdk.navigation.online.Configuration
import com.tomtom.sdk.navigation.online.OnlineTomTomNavigationFactory
import com.tomtom.sdk.navigation.ui.NavigationFragment
import com.tomtom.sdk.navigation.ui.NavigationUiOptions
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.RoutePlanningCallback
import com.tomtom.sdk.routing.RoutePlanningResponse
import com.tomtom.sdk.routing.RoutingFailure
import com.tomtom.sdk.routing.online.OnlineRoutePlanner
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.options.guidance.ExtendedSections
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.routing.options.guidance.InstructionPhoneticsType
import com.tomtom.sdk.routing.route.Route
import com.tomtom.sdk.vehicle.Vehicle
import com.tomtom.sdk.vehicle.VehicleProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

/**
 * ViewModel class for MapScreen handling the map and the navigation.
 * @param application The application context.
 */
class MapViewModel(application: Application): AndroidViewModel(application), MapDialog{

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    private lateinit var mapFragment: MapFragment
    private lateinit var navigationFragment: NavigationFragment

    private lateinit var tomTomMap: TomTomMap

    private lateinit var navigationTileStore: NavigationTileStore
    private lateinit var locationProvider: LocationProvider
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener
    private lateinit var routePlanner: RoutePlanner
    private var route: Route? = null
    private lateinit var routePlanningOptions: RoutePlanningOptions
    private lateinit var tomTomNavigation: TomTomNavigation

    private val apiKey = BuildConfig.TOMTOM_API_KEY


    /**
     * Function to initialize the map and start user location procedure
     * @param mapFragmentView The map fragment view.
     */
    fun initMap(mapFragmentView: MapFragment){
        mapFragment = mapFragmentView

        mapFragment.getMapAsync { map ->
            tomTomMap = map
            enableUserLocation()
            mapFragment.zoomControlsView.isVisible = true
            mapFragment.scaleView.units = UnitSystem.Metric
            mapFragment.logoView.visibilityPolicy = LogoView.VisibilityPolicy.Invisible
        }
    }

    /** The [NavigationTileStore] class is used to get
    * tile data based on online maps. This is necessary since the component
    * navigation is based on data from the navigation tiles.
    *
    * A "tile" is a small square portion of a digital map. The maps come
    * divided into these tiles for easy loading and viewing,
    * allowing you to load only the sections you need based on location and level
    * user zoom.
     */
     fun initNavigationTileStore() {
        navigationTileStore = NavigationTileStore.create(
            context = getApplication<Application>().applicationContext,
            navigationTileStoreConfig = NavigationTileStoreConfiguration(
                apiKey = apiKey
            )
        )
     }

    /**
     * The [LocationProvider] interface is used to get current location updates.
     * In this case [AndroidLocationProvider] is used.
     */
    fun initLocationProvider() {
        locationProvider = AndroidLocationProvider(
            context = getApplication<Application>().applicationContext
        )
    }

    /**
     * Initialization of routePlanner for online routing
     */
    fun initRouting() {
        routePlanner = OnlineRoutePlanner.create(
            context = getApplication<Application>().applicationContext,
            apiKey = apiKey
        )
    }

    /**
     * Initialization of the main navigation component
     */
    fun initNavigation() {
        val configuration = Configuration(
            context = getApplication<Application>().applicationContext,
            navigationTileStore = navigationTileStore,
            locationProvider = locationProvider,
            routePlanner = routePlanner,
            vehicleProvider = VehicleProviderFactory.create(vehicle = Vehicle.Car())
        )
        tomTomNavigation = OnlineTomTomNavigationFactory.create(configuration)
    }

    /**
     * Function to require location permissions if not already granted and show user location
     */
    private fun enableUserLocation() {
        if (areLocationPermissionsGranted())
            showUserLocation()
         else
            _uiState.update { it.copy(requestLocationPermissions = true) }
    }

    /**
     * Function to use user location to display on marker on the map
     * and change camera position over the marker
     */
    fun showUserLocation() {
        locationProvider.enable()
        onLocationUpdateListener = OnLocationUpdateListener { location ->
            setLocationDialogState()
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 10.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }
        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
        tomTomMap.setLocationProvider(locationProvider)
        val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
        tomTomMap.enableLocationMarker(locationMarker)
    }

    private fun isNavigationRunning(): Boolean = tomTomNavigation.navigationSnapshot != null

    /**
     * Function that calculate route to the user from current location of the driver
     * @param userLocation The location of the user
     */
    suspend fun calculateRouteTo(userLocation:String) {
        val destination = getUserLocation(userLocation)
        val driverLocation = tomTomMap.currentLocation?.position ?: return
        val itinerary = Itinerary(origin = driverLocation, destination = destination!!)
        routePlanningOptions = RoutePlanningOptions(
            itinerary = itinerary,
            guidanceOptions = GuidanceOptions(
                phoneticsType = InstructionPhoneticsType.Ipa,
                extendedSections = ExtendedSections.All,
                language = Locale.ITALIAN
            ),
            vehicle = Vehicle.Car()
        )
        routePlanner.planRoute(routePlanningOptions, routePlanningCallback)
    }

    /**
     * Function that performs geocoding operation calling the API.
     * @param userLocation The location of the user as address string
     * @return The [GeoPoint] object representing the latitude and longitude of the user location
     */
    private suspend fun getUserLocation(userLocation: String): GeoPoint? = withContext(Dispatchers.Main) {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            try {
                val response = RetrofitObject.geocodingService.getUserLocation(userLocation, apiKey)
                if (response.isSuccessful && response.body()!!.results.isNotEmpty()) {
                    Log.v("MapViewModel", response.body().toString())
                    val lat = response.body()!!.results[0].position.lat
                    val lon = response.body()!!.results[0].position.lon
                    GeoPoint(lat, lon)
                } else {
                    Log.d("MapViewModel", "Not successful")
                    null
                }
            } catch (e: IOException) {
                Log.d("MapViewModel", "No internet connection")
                null
            } catch (e: HttpException) {
                Log.d("MapViewModel", "Unexpected response")
                null
            }
        }
        deferred.await()
    }

    /**
     * Callback function used to handle the response from the route planning request
     */
    private val routePlanningCallback = object : RoutePlanningCallback {
        override fun onSuccess(result: RoutePlanningResponse) {
            route = result.routes.first()
            drawRoute(route!!)
            _uiState.update { it.copy(route = route) }
            tomTomMap.zoomToRoutes(ZOOM_TO_ROUTE_PADDING)
        }


        override fun onFailure(failure: RoutingFailure) {
            Log.e("map", failure.toString())
        }

        override fun onRoutePlanned(route: Route) = Unit
    }

    /**
     * Function to draw the route on the map
     * @param route The route to be drawn
     */
    private fun drawRoute(route: Route) {
        val instructions = route.mapInstructions()
        val routeOptions = RouteOptions(
            geometry = route.geometry,
            destinationMarkerVisible = true,
            departureMarkerVisible = true,
            instructions = instructions,
            routeOffset = route.routePoints.map { it.routeOffset }
        )
        tomTomMap.addRoute(routeOptions)
    }

    /**
     * Function that display on the map instructions for the navigation
     */
    private fun Route.mapInstructions(): List<Instruction> {
        val routeInstructions = legs.flatMap { routeLeg -> routeLeg.instructions }
        return routeInstructions.map {
            Instruction(routeOffset = it.routeOffset)
        }
    }

    /**
        Used to start navigation via
     * - initialization of NavigationFragment to display UI navigation information,
     * - passing the Route object along which navigation will be carried out and the RoutePlanningOptions used during route planning,
     * - management of updates to navigation states using the NavigationListener.
     */
    fun startNavigation(route: Route, orderId: String?) {
        if (!isNavigationRunning()) {
            _uiState.update {
                it.copy(
                    mapHeight = Resources.getSystem().displayMetrics.widthPixels.dp,
                    mapWidth = Resources.getSystem().displayMetrics.widthPixels.dp
                )
            }
            setOrderStatus(
                orderId = orderId!!,
                "in consegna"
            )
            mapFragment.currentLocationButton.visibilityPolicy = CurrentLocationButton.VisibilityPolicy.Invisible
            navigationFragment.setTomTomNavigation(tomTomNavigation)
            val routePlan = RoutePlan(route, routePlanningOptions)
            navigationFragment.startNavigation(routePlan)
            navigationFragment.addNavigationListener(navigationListener)
            tomTomNavigation.addProgressUpdatedListener(progressUpdatedListener)
            tomTomNavigation.addActiveRouteChangedListener(activeRouteChangedListener)
        }
    }


    /**
     * Function to initialize the NavigationFragment where the navigation info are displayed
     * @param binding The binding object of the layout
     * @param fragmentManager The fragment manager
     * @param orderId The order id
     */
      fun initNavigationFragment(
        binding: MapLayoutBinding,
        fragmentManager: FragmentManager?,
        orderId: String?
    ) {
          if(!::navigationFragment.isInitialized) {
              navigationFragment = NavigationFragment.newInstance(
                  NavigationUiOptions(
                      keepInBackground = true,
                      unitSystemType = UnitSystemType.Dynamic(UnitSystem.Metric),
                      voiceLanguage = Locale.ITALIAN
                  )
              )
          }

        fragmentManager?.beginTransaction()?.apply {
            replace(binding.navigationFragmentContainer.id, navigationFragment)
            commitNow()
        }

        navigationFragment.navigationView.setArrivalViewButtonClickListener{
            stopNavigation()
            setOrderStatus(
                orderId = orderId!!,
                "consegnato"
            )
            setDialogState(true)
        }

        _uiState.update { it.copy(binding = binding) }
    }

    /**
     * CameraChangeListener is used to observe the tracking mode of the camera and detect whether the camera is stuck on the arrow.
     * If the user starts to move the camera, it will change and you can adjust the user interface accordingly.
     * Once navigation is started, the camera is set to follow the user's location and the location indicator is changed to an arrow.
     * To match raw location updates to routes, use MapMatchedLocationProvider and set it to TomTomMap.
     */
    private val navigationListener = object : NavigationFragment.NavigationListener {
        override fun onStarted() {
            tomTomMap.addCameraChangeListener(cameraChangeListener)
            tomTomMap.cameraTrackingMode = CameraTrackingMode.FollowRouteDirection
            tomTomMap.enableLocationMarker(LocationMarkerOptions(LocationMarkerOptions.Type.Chevron))
            setMapMatchedLocationProvider()
            // use for simulation purposes only
            setSimulationLocationProviderToNavigation(route!!)
            tomTomMap.setPadding(Padding(0, 0, 0, getApplication<Application>().resources.getDimensionPixelOffset(R.dimen.map_padding_bottom)))
        }

        override fun onStopped() {
            stopNavigation()
        }
    }

    /**
     * Function to set the location provider to simulation mode
     */
    private fun setSimulationLocationProviderToNavigation(route: Route) {
        val routeGeoLocations = route.geometry.map { GeoLocation(it) }
        val simulationStrategy = InterpolationStrategy(routeGeoLocations)
        locationProvider = SimulationLocationProvider.create(strategy = simulationStrategy)
        tomTomNavigation.locationProvider = locationProvider
        locationProvider.enable()
    }

    /**
     * Function to update the progress of the navigation like remaining time, distance, ecc..
     */
    private val progressUpdatedListener = ProgressUpdatedListener {
        tomTomMap.routes.first().progress = it.distanceAlongRoute
    }

    /**
     * Function to update the route when the route is changed during navigation
     */
    private val activeRouteChangedListener by lazy {
        ActiveRouteChangedListener { route ->
            tomTomMap.removeRoutes()
            drawRoute(route)
        }
    }

    /**
     * Function to stop the navigation and reset the map state, removes all listeners
     * and enable the user location to be displayed on the map again
     */
    private fun stopNavigation() {
        resetMapSize()
        navigationFragment.stopNavigation()
        mapFragment.currentLocationButton.visibilityPolicy =
            CurrentLocationButton.VisibilityPolicy.InvisibleWhenRecentered
        tomTomMap.removeCameraChangeListener(cameraChangeListener)
        tomTomMap.cameraTrackingMode = CameraTrackingMode.None
        tomTomMap.enableLocationMarker(LocationMarkerOptions(LocationMarkerOptions.Type.Pointer))
        tomTomMap.setPadding(Padding(0, 0, 0, 0))
        navigationFragment.removeNavigationListener(navigationListener)
        tomTomNavigation.removeProgressUpdatedListener(progressUpdatedListener)
        tomTomNavigation.removeActiveRouteChangedListener(activeRouteChangedListener)
        initLocationProvider()
        enableUserLocation()
    }


    private fun setMapMatchedLocationProvider() {
        val mapMatchedLocationProvider = MapMatchedLocationProvider(tomTomNavigation)
        tomTomMap.setLocationProvider(mapMatchedLocationProvider)
        mapMatchedLocationProvider.enable()
    }

    /**
     * Function to observe the camera change listener and detect when the camera is stuck on the arrow.
     */
    private val cameraChangeListener by lazy {
        CameraChangeListener {
            val cameraTrackingMode = tomTomMap.cameraTrackingMode
            if (cameraTrackingMode == CameraTrackingMode.FollowRouteDirection) {
                navigationFragment.navigationView.showSpeedView()
            } else {
                navigationFragment.navigationView.hideSpeedView()
            }
        }
    }

    /**
     * Function to check user permissions for location
     */
    private fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
        getApplication<Application>().applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        getApplication<Application>().applicationContext,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    override fun setOrderConclude(orderId: String) {
        setOrderStatus(
            orderId = orderId,
            "concluso"
        )
    }

    /**
     * Function to update the order status in the database
     */
    private fun setOrderStatus(orderId: String,status: String){
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { document ->
                    val order = document.documents[0].reference
                    order.update("status", status)
                }
        }
    }


    override fun setDialogState(state: Boolean) {
        _uiState.update { it.copy(openDialog = state) }
    }


    private fun setLocationDialogState() {
        _uiState.update { it.copy(locationAcquired = true) }
    }

    private fun resetMapSize() = run { _uiState.update { it.copy(mapHeight = 600.dp, mapWidth = 400.dp) } }

    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 200
    }

}