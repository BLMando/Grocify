plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

val tomtomApiKey: String by project

android {
    namespace = "com.example.grocify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.grocify"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    buildTypes.configureEach {
        buildConfigField("String", "TOMTOM_API_KEY", "\"$tomtomApiKey\"")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }
}

dependencies {
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")


    //FIREBASE LIBRARIES
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:21.0.0")


    //ANDROID X COMPOSE LIBRARIES
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.ui:ui-viewbinding:1.6.7")
    implementation("androidx.core:core-splashscreen:1.1.0-rc01")
    implementation("androidx.compose.material:material-icons-extended:1.7.0-beta01")
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //BARCODE E QRCODE LIBRARY
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //RETROFIT
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    //ROOM DB
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")




    //BAR CODE LIBRARY
    implementation("com.google.android.gms:play-services-code-scanner:16.0.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")
    implementation("com.google.mlkit:barcode-scanning:17.1.0")
    implementation(libs.androidx.camera.core)

    //QRCODE LIBRARY
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("androidx.multidex:multidex:2.0.1")

    //CHART LIBRARY
    implementation("com.himanshoe:charty:2.0.0-alpha01")

    //COIL FOR IMAGE LOADING
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")


    //TOMTOM MAPS API
    val version = "1.4.0"
    implementation("com.tomtom.sdk.location:provider-android:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.location:provider-map-matched:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.location:provider-simulation:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.maps:map-display:$version") {
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.datamanagement:navigation-tile-store:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.navigation:navigation-online:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.navigation:route-replanner-online:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.navigation:ui:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }
    implementation("com.tomtom.sdk.routing:route-planner-online:$version"){
        exclude(group = "com.google.protobuf", module = "proto-google-common-protos")
        exclude(group = "com.google.protobuf", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "module:'protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.protobuf", module = "protobuf-kotlin")
    }


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("reflect"))
}