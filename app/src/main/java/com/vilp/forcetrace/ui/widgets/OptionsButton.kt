package com.vilp.forcetrace.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vilp.forcetrace.R
import com.vilp.forcetrace.ui.theme.ForceTraceTheme

@Composable
fun OptionsButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    description: String = "Option button",
    onClick: () -> Unit
) {
    IconButton(modifier = modifier.padding(horizontal = 8.dp), onClick = onClick) {
        Icon(painterResource(id), description)
    }
}

@Preview(name = "Redo button", widthDp = 100, heightDp = 100)
@Composable
fun PreviewRedoOption() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_redo_24) {
                    println("Algo")
                }
            }
        }
    }
}

@Preview(name = "Undo button", widthDp = 100, heightDp = 100)
@Composable
fun PreviewUndoOption() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_undo_24) {
                    println("Algo")
                }
            }
        }
    }
}

@Preview(name = "Clear button", widthDp = 100, heightDp = 100)
@Composable
fun PreviewClearOption() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_clear_24) {
                    println("Algo")
                }
            }
        }
    }
}

@Preview(name = "Export button", widthDp = 100, heightDp = 100)
@Composable
fun PreviewExportOption() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_download_24) {
                    println("Algo")
                }
            }
        }
    }
}

@Preview(name = "Erase button", widthDp = 100, heightDp = 100)
@Composable
fun PreviewEraseOption() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_design_services_24) {
                    println("Algo")
                }
            }
        }
    }
}

@Preview(name = "Row bottom bar", widthDp = 320, heightDp = 100)
@Composable
fun PreviewRowBottomBar() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                OptionsButton(id = R.drawable.baseline_design_services_24) {
                    println("Algo")
                }
                OptionsButton(id = R.drawable.baseline_clear_24) {
                    println("Algo")
                }
                OptionsButton(id = R.drawable.baseline_undo_24) {
                    println("Algo")
                }
                OptionsButton(id = R.drawable.baseline_redo_24) {
                    println("Algo")
                }
                OptionsButton(id = R.drawable.baseline_download_24) {
                    println("Algo")
                }
            }
        }
    }
}