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
import com.example.grocify.api.RetrofitObject
import com.example.grocify.data.MapUiState
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
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class MapViewModel(application: Application): AndroidViewModel(application){

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
     * Inizializzazione della mappa mediante il riferimento alla view ottenuto dal AndroidViewBinding
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

    /**
     * La classe [NavigationTileStore] viene utilizzata per ottenere
     * dati delle tile basati sulle mappe online. Questo è necessario poiché il componente
     * di navigazione si basa sui dati delle tile di navigazione.
     *
     * Una "tile" è una piccola porzione quadrata di una mappa digitale. Le mappe vengono
     * suddivise in queste piastrelle per facilitare il caricamento e la visualizzazione,
     * permettendo di caricare solo le sezioni necessarie in base alla posizione e al livello
     * di zoom dell'utente.
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
     * L'interfaccia [LocationProvider] viene usata per ottenere gli aggiornamenti sulla posizione attuale.
     * In questo caso viene usato [AndroidLocationProvider].
     */
    fun initLocationProvider() {
        locationProvider = AndroidLocationProvider(
            context = getApplication<Application>().applicationContext
        )
    }

    /**
     * Inizializzazione del routePlanner per la pianificazione delle rotte
     */
    fun initRouting() {
        routePlanner = OnlineRoutePlanner.create(
            context = getApplication<Application>().applicationContext,
            apiKey = apiKey
        )
    }

    /**
     * Usiamo tutte le informazioni precedenti per inizializzare il componente di navigation
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
     * Per mostrare la posizione dell'utente,
     * l'applicazione deve utilizzare i servizi di localizzazione del dispositivo,
     * che richiedono le autorizzazioni appropriate.
     */
    private fun enableUserLocation() {
        if (areLocationPermissionsGranted())
            showUserLocation()
         else
            _uiState.update { it.copy(requestLocationPermissions = true) }
    }

    /**
     *
     * Il LocationProvider stesso segnala solo i cambiamenti di posizione. Non interagisce internamente con la mappa o la navigazione.
     * Pertanto, per mostrare la posizione dell'utente sulla mappa, è necessario impostare il LocationProvider su TomTomMap.
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
     * Crea la rotta a partire dalla posizione correte fino alla destinazione selezionata
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

    private suspend fun getUserLocation(userLocation: String): GeoPoint? = withContext(Dispatchers.Main) {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            try {
                val response = RetrofitObject.apiService.getUserLocation(userLocation, apiKey)
                if (response.isSuccessful && response.body() != null) {
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
     * Questa funzione di callback serve per ottenere l'oggetto che rappresenta la rotta
     * e disegnarla sulla mappa.
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
     * Fornisce istruzioni disegnate sul percorso sotto forma di frecce che indicano le manovre.
     */
    private fun Route.mapInstructions(): List<Instruction> {
        val routeInstructions = legs.flatMap { routeLeg -> routeLeg.instructions }
        return routeInstructions.map {
            Instruction(routeOffset = it.routeOffset)
        }
    }

    /**
        Utilizzato per avviare la navigazione mediante
     * - inizializzazione del NavigationFragment per visualizzare le informazioni di navigazione dell'interfaccia utente,
     * - passaggio dell'oggetto Route lungo il quale verrà effettuata la navigazione e delle RoutePlanningOptions utilizzate durante la pianificazione del percorso,
     * - gestione degli aggiornamenti agli stati di navigazione utilizzando il NavigationListener.
     */
    fun startNavigation(route: Route) {
        if (!isNavigationRunning()) {
            _uiState.update {
                it.copy(
                    mapHeight = Resources.getSystem().displayMetrics.widthPixels.dp,
                    mapWidth = Resources.getSystem().displayMetrics.widthPixels.dp
                )
            }
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
     * Inizializzazione del NavigationFragment per la navigazione
     */
      fun initNavigationFragment(binding: MapLayoutBinding, fragmentManager: FragmentManager?) {
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
            setDialogState(true)
        }

        _uiState.update { it.copy(binding = binding) }
    }

    /**
     * - Viene utilizzato CameraChangeListener per osservare la modalità di tracciamento della fotocamera e rilevare se la fotocamera è bloccata sulla freccia. Se l'utente inizia a muovere la fotocamera, questa cambierà e è possibile regolare l'interfaccia utente di conseguenza.
     * - Una volta avviata la navigazione, la fotocamera viene impostata per seguire la posizione dell'utente e l'indicatore di posizione viene cambiato in una freccia. Per abbinare gli aggiornamenti di posizione grezzi ai percorsi, utilizzare MapMatchedLocationProvider e impostarlo su TomTomMap.
     */
    private val navigationListener = object : NavigationFragment.NavigationListener {
        override fun onStarted() {
            tomTomMap.addCameraChangeListener(cameraChangeListener)
            tomTomMap.cameraTrackingMode = CameraTrackingMode.FollowRouteDirection
            tomTomMap.enableLocationMarker(LocationMarkerOptions(LocationMarkerOptions.Type.Chevron))
            setMapMatchedLocationProvider()
            setSimulationLocationProviderToNavigation(route!!)
            tomTomMap.setPadding(Padding(0, 0, 0, getApplication<Application>().resources.getDimensionPixelOffset(R.dimen.map_padding_bottom)))
        }

        override fun onStopped() {
            stopNavigation()
        }
    }

    private fun setSimulationLocationProviderToNavigation(route: Route) {
        val routeGeoLocations = route.geometry.map { GeoLocation(it) }
        val simulationStrategy = InterpolationStrategy(routeGeoLocations)
        locationProvider = SimulationLocationProvider.create(strategy = simulationStrategy)
        tomTomNavigation.locationProvider = locationProvider
        locationProvider.enable()
    }

    private val progressUpdatedListener = ProgressUpdatedListener {
        tomTomMap.routes.first().progress = it.distanceAlongRoute
    }

    private val activeRouteChangedListener by lazy {
        ActiveRouteChangedListener { route ->
            tomTomMap.removeRoutes()
            drawRoute(route)
        }
    }

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


    private fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
        getApplication<Application>().applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        getApplication<Application>().applicationContext,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    fun setOrderConclude(orderId: String) {
        db.collection("orders")
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener { document ->
                val order = document.documents[0].reference
                order.update("status", "concluso")
            }
    }

    fun setDialogState(openDialog: Boolean) {
        _uiState.update { it.copy(openDialog = openDialog) }
    }

    private fun setLocationDialogState() {
        _uiState.update { it.copy(locationAcquired = true) }
    }

    private fun resetMapSize() = run { _uiState.update { it.copy(mapHeight = 600.dp, mapWidth = 400.dp) } }

    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 200
    }

}