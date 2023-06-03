object Deps {

    object Compose {
        val ui by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
        val uiTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.compose}" }
        val uiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
        val uiViewBinding by lazy { "androidx.compose.ui:ui-viewbinding:${Versions.compose}" }
        val materialIconsExtended by lazy { "androidx.compose.material:material-icons-extended:${Versions.compose}" }
        val activity by lazy { "androidx.activity:activity-compose:${Versions.activity}" }
        val runtimeLiveData by lazy { "androidx.compose.runtime:runtime-livedata:${Versions.compose}" }
    }

    object Lifecycle {
        val viewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}" }
        val viewModelKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" }
        val runtimeCompose by lazy { "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}" }
    }

    object ComposeAndroidTest

}
