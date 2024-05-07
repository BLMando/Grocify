plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
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
        jvmTarget = "1.8"
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
    //FIREBASE LIBRARIES
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:21.0.0")


    //ANDROID X COMPOSE LIBRARIES
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.ui:ui-viewbinding:1.6.6")
    implementation("androidx.core:core-splashscreen:1.1.0-rc01")
    implementation("androidx.compose.material:material-icons-extended:1.7.0-alpha07")
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)

    //QRCODE LIBRARY
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.androidx.constraintlayout)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("androidx.multidex:multidex:2.0.1")

    //CHART LIBRARY
    implementation("com.himanshoe:charty:2.0.0-alpha01")


    //TOMTOM MAPS API
    val version = "0.50.6"
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
}