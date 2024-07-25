package com.sublime.lingo.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sublime.lingo.presentation.ui.theme.LingoTheme
import com.sublime.lingo.presentation.ui.viewmodel.TranslationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LingoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TranslationScreen()
                }
            }
        }
    }
}

@Composable
fun TranslationScreen(viewModel: TranslationViewModel = hiltViewModel()) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
//        OutlinedTextField(
//            value = viewModel.inputText,
//            onValueChange = { viewModel.inputText = it },
//            label = { Text("Enter text to translate") },
//            modifier = Modifier.fillMaxWidth(),
//        )

//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            LanguageSelector(
//                selected = viewModel.sourceLanguage,
//                onSelected = { viewModel.sourceLanguage = it },
//                label = "From",
//            )
//            LanguageSelector(
//                selected = viewModel.targetLanguage,
//                onSelected = { viewModel.targetLanguage = it },
//                label = "To",
//            )
//        }

//        Button(
//            onClick = { viewModel.translate() },
//            modifier = Modifier.align(Alignment.End),
//        ) {
//            Text("Translate")
//        }

//        Text(
//            text = viewModel.outputText,
//            modifier =
//                Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//        )
    }
}

@Composable
fun LanguageSelector(
    selected: String,
    onSelected: (String) -> Unit,
    label: String,
) {
//    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German", "Italian")

    Column {
        Text(label)
//        Box {
//            OutlinedButton(onClick = { expanded = true }) {
//                Text(selected)
//            }
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//            ) {
//                languages.forEach { language ->
//                    DropdownMenuItem(
//                        text = { Text(language) },
//                        onClick = {
//                            onSelected(language)
//                            expanded = false
//                        },
//                    )
//                }
//            }
//        }
    }
}
