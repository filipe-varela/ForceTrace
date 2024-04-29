//   Copyright 2024 Filipe André Varela
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.vilp.forcetrace.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vilp.forcetrace.ui.theme.ForceTraceTheme

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment = Alignment.BottomEnd,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable (RowScope.() -> Unit)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(horizontalAlignment).fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            content = content
        )
    }
}

@Preview(name = "Bottom Bar", widthDp = 320, heightDp = (320 * 9f/16f).toInt())
@Composable
fun PreviewBottomBar() {
    ForceTraceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                Text("Algo 1")
                Text("Algo 2")
                Text("Algo 3")
            }
        }
    }
}