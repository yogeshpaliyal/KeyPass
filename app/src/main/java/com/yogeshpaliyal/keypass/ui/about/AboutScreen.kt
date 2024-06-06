package com.yogeshpaliyal.keypass.ui.about

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.utils.openLink
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem

@Composable
fun AboutScreen() {
    Scaffold(bottomBar = { DefaultBottomAppBar() }) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            MainContent()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainContentDarkPreview() {
    MainContent()
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun MainContentLightPreview() {
    MainContent()
}

@Composable
private fun MainContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground,
                    shape = androidx.compose.foundation.shape.AbsoluteRoundedCornerShape(8.dp)
                )
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "App Icon"
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(BuildConfig.VERSION_NAME)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                stringResource(id = R.string.app_desc),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.source_code),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.openLink("https://github.com/yogeshpaliyal/KeyPass")
                    },
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                style = MaterialTheme.typography.labelMedium,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }

        Text(
            stringResource(id = R.string.app_developer),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            PreferenceItem(
                painter = painterResource(id = R.drawable.ic_twitter),
                title = R.string.app_developer_x
            ) {
                context.openLink("https://twitter.com/yogeshpaliyal")
            }
        }

        PreferenceItem(
            painter = painterResource(id = R.drawable.ic_github),
            title = R.string.app_developer_github
        ) {
            context.openLink("https://github.com/yogeshpaliyal")
        }

        PreferenceItem(
            painter = painterResource(id = R.drawable.ic_linkedin),
            title = R.string.app_developer_linkedin
        ) {
            context.openLink("https://linkedin.com/in/yogeshpaliyal")
        }
    }
}
