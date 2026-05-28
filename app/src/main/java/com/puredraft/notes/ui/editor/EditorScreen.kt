package com.puredraft.notes.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Title
import com.puredraft.notes.utils.MarkdownVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.puredraft.notes.utils.BiometricAuthenticator
import com.puredraft.notes.utils.findFragmentActivity
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import com.puredraft.notes.theme.glassmorphism

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var contentState by remember(uiState.id) {
        mutableStateOf(TextFieldValue(uiState.content, TextRange(uiState.content.length)))
    }

    // Save instantly when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveNoteInstantly()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2C1E3F), Color(0xFF100A1D), Color(0xFF000000)),
                    radius = 1500f
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.togglePin() }) {
                        Icon(
                            imageVector = if (uiState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { 
                        if (!uiState.isLocked) {
                            val activity = context.findFragmentActivity()
                            if (activity != null) {
                                coroutineScope.launch {
                                    val result = BiometricAuthenticator.authenticate(activity)
                                    if (result is BiometricAuthenticator.Result.Success) {
                                        viewModel.toggleLock()
                                    } else if (result is BiometricAuthenticator.Result.NotAvailable) {
                                        android.widget.Toast.makeText(context, "No screen lock setup. Locking anyway.", android.widget.Toast.LENGTH_SHORT).show()
                                        viewModel.toggleLock()
                                    } else {
                                        android.widget.Toast.makeText(context, "Authentication failed.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                viewModel.toggleLock()
                            }
                        } else {
                            // Automatically unlock without pin if they are already inside the editor
                            viewModel.toggleLock()
                        }
                    }) {
                        Icon(
                            imageVector = if (uiState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                placeholder = {
                    Text(
                        "Title",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            TextField(
                value = contentState,
                onValueChange = {
                    contentState = it
                    viewModel.onContentChanged(it.text)
                },
                placeholder = {
                    Text(
                        "Start typing...",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                visualTransformation = MarkdownVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            // Rich Text Formatting Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .glassmorphism()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                fun insertMarkdown(syntax: String) {
                    val text = contentState.text
                    val start = minOf(contentState.selection.start, contentState.selection.end)
                    val end = maxOf(contentState.selection.start, contentState.selection.end)
                    
                    if (start == end) return
                    
                    val selectedText = text.substring(start, end)
                    
                    val newText = text.substring(0, start) + syntax + selectedText + syntax + text.substring(end)
                    val newSelection = start + syntax.length
                    
                    contentState = TextFieldValue(
                        text = newText,
                        selection = TextRange(newSelection, newSelection + selectedText.length)
                    )
                    viewModel.onContentChanged(newText)
                }
                
                fun insertPrefix(prefix: String) {
                    val text = contentState.text
                    val start = minOf(contentState.selection.start, contentState.selection.end)
                    val end = maxOf(contentState.selection.start, contentState.selection.end)
                    
                    if (start == end) return
                    
                    val newText = text.substring(0, start) + prefix + text.substring(start)
                    contentState = TextFieldValue(
                        text = newText,
                        selection = TextRange(start + prefix.length)
                    )
                    viewModel.onContentChanged(newText)
                }

                IconButton(onClick = { insertPrefix("### ") }) {
                    Icon(Icons.Default.Title, contentDescription = "Heading", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { insertMarkdown("**") }) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { insertMarkdown("*") }) {
                    Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { insertMarkdown("__") }) {
                    Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { insertMarkdown("~~") }) {
                    Icon(Icons.Default.FormatStrikethrough, contentDescription = "Strikethrough", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { insertPrefix("- ") }) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

}
