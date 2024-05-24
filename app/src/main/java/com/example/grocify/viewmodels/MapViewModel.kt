package com.example.grocify.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.BuildConfig
import com.example.grocify.R
import com.example.grocify.data.MapUiState
import com.example.grocify.databinding.MapLayoutBinding
import com.tomtom.sdk.common.measures.UnitSystem
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStore
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStoreConfiguration
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.mapmatched.MapMatchedLocationProvider
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraChangeListener
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.camera.CameraTrackingMode
import com.tomtom.sdk.map.display.common.screen.Padding
import com.tomtom.sdk.map.display.gesture.MapLongClickListener
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteClickListener
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.map.display.ui.currentlocation.CurrentLocationButton
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
import com.tomtom.sdk.routing.options.calculation.WaypointOptimization
import com.tomtom.sdk.routing.options.guidance.ExtendedSections
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.routing.options.guidance.InstructionPhoneticsType
import com.tomtom.sdk.routing.route.Route
import com.tomtom.sdk.vehicle.Vehicle
import com.tomtom.sdk.vehicle.VehicleProviderFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class MapViewModel(application: Application): AndroidViewModel(application){

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

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
     * Inizializzazione della mappa mediante il riferimento alla view ottenuto dal AndroiudViewBindig
     */
    fun initMap(mapFragmentView: MapFragment){
        mapFragment = mapFragmentView

        mapFragment.getMapAsync{ map ->
            tomTomMap = map
            enableUserLocation()
            setUpMapListeners()
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
        if (areLocationPermissionsGranted()) {
            showUserLocation()
        } else {
            _uiState.update {
                it.copy(
                    requestLocationPermissions = true
                )
            }
        }
    }

    /**
     *
     * Il LocationProvider stesso segnala solo i cambiamenti di posizione. Non interagisce internamente con la mappa o la navigazione.
     * Pertanto, per mostrare la posizione dell'utente sulla mappa, è necessario impostare il LocationProvider su TomTomMap.
     */
    fun showUserLocation() {
        locationProvider.enable()
        //zoom alla posizione corrente
        onLocationUpdateListener = OnLocationUpdateListener { location ->
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 8.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }
        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
        tomTomMap.setLocationProvider(locationProvider)
        val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
        tomTomMap.enableLocationMarker(locationMarker)
    }


    private fun setUpMapListeners() {
        tomTomMap.addMapLongClickListener(mapLongClickListener)
        //tomTomMap.addRouteClickListener(routeClickListener)
    }

    private val mapLongClickListener = MapLongClickListener { geoPoint ->
        tomTomMap.clear()
        calculateRouteTo(geoPoint)
        true
    }

    private fun isNavigationRunning(): Boolean = tomTomNavigation.navigationSnapshot != null

    /**
     * Utilizzato per avviare la navigazione basata su un percorso selezionato, se la navigazione non è già in esecuzione.
     */
    private val routeClickListener = RouteClickListener {
        if (!isNavigationRunning()) {
            route?.let { route ->
                //viene nasconsto il pulsante della posizione
                mapFragment.currentLocationButton.visibilityPolicy = CurrentLocationButton.VisibilityPolicy.Invisible
                startNavigation(route)
            }
        }
    }

    /**
     * Crea la rotta a partire dalla posizione correte fino alla destinazione selezionata
     */
    private fun calculateRouteTo(destination: GeoPoint) {
        val userLocation = tomTomMap.currentLocation?.position ?: return
        val itinerary = Itinerary(origin = userLocation, destination = destination)
        routePlanningOptions = RoutePlanningOptions(
            itinerary = itinerary,
            guidanceOptions = GuidanceOptions(
                phoneticsType = InstructionPhoneticsType.Ipa,
                extendedSections = ExtendedSections.All,
                language = Locale.ITALIAN
            ),
            vehicle = Vehicle.Car(),
            waypointOptimization = WaypointOptimization.DistanceBased
        )
        routePlanner.planRoute(routePlanningOptions, routePlanningCallback)
    }

    /**
     * Questa funzione di callback serve per ottenere l'oggetto che rappresenta la rotta
     * e disegnarla sulla mappa.
     */
    private val routePlanningCallback = object : RoutePlanningCallback {
        override fun onSuccess(result: RoutePlanningResponse) {
            route = result.routes.first()
            _uiState.update {
                it.copy(route = route)
            }
            drawRoute(route!!)
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
            Instruction(
                routeOffset = it.routeOffset,
                combineWithNext = true
            )
        }
    }

    /**
        Utilizzato per avviare la navigazione mediante
     * - inizializzazione del NavigationFragment per visualizzare le informazioni di navigazione dell'interfaccia utente,
     * - passaggio dell'oggetto Route lungo il quale verrà effettuata la navigazione e delle RoutePlanningOptions utilizzate durante la pianificazione del percorso,
     * - gestione degli aggiornamenti agli stati di navigazione utilizzando il NavigationListener.
     */
    fun startNavigation(route: Route) {
        navigationFragment.setTomTomNavigation(tomTomNavigation)
        val routePlan = RoutePlan(route, routePlanningOptions)
        navigationFragment.startNavigation(routePlan)
        navigationFragment.addNavigationListener(navigationListener)
        tomTomNavigation.addProgressUpdatedListener(progressUpdatedListener)
        tomTomNavigation.addActiveRouteChangedListener(activeRouteChangedListener)
    }


    /**
     * Inizializzazione del NavigationFragment per la navigazione
     */
      fun initNavigationFragment(binding: MapLayoutBinding, fragmentManager: FragmentManager?) {
          if(!::navigationFragment.isInitialized)
             navigationFragment = NavigationFragment.newInstance(
                NavigationUiOptions(
                    keepInBackground = true,
                    voiceLanguage = Locale.ITALIAN,
                    unitSystemType = UnitSystemType.Dynamic(UnitSystem.Metric),
                    adaptVoiceLanguage = true
                )
            )

        fragmentManager?.beginTransaction()?.apply {
            replace(binding.navigationFragmentContainer.id, navigationFragment)
            commitNow()
        }

        _uiState.update {
            it.copy(
                binding = binding
            )
        }
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
            tomTomMap.setPadding(Padding(0, 0, 0, getApplication<Application>().resources.getDimensionPixelOffset(R.dimen.map_padding_bottom)))
        }

        override fun onStopped() {
            stopNavigation()
        }
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

    fun stopNavigation() {
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
        tomTomMap.clear()
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

    /*fun onDestroy() {
        tomTomMap.setLocationProvider(null)
        super.onDestroy()
        tomTomNavigation.close()
        navigationTileStore.close()
        locationProvider.close()
    }*/

    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 100
    }
}