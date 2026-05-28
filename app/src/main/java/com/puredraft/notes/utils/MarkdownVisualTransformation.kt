package com.puredraft.notes.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

class MarkdownVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val builder = AnnotatedString.Builder(text.text)

        // Bold: **text**
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        boldRegex.findAll(text.text).forEach { matchResult ->
            builder.addStyle(
                style = SpanStyle(fontWeight = FontWeight.Bold),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            // Hide the markdown syntax itself
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.first,
                end = matchResult.range.first + 2
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.last - 1,
                end = matchResult.range.last + 1
            )
        }

        // Italic: *text* (avoiding ** overlaps)
        val italicRegex = Regex("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)")
        italicRegex.findAll(text.text).forEach { matchResult ->
            builder.addStyle(
                style = SpanStyle(fontStyle = FontStyle.Italic),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.first,
                end = matchResult.range.first + 1
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.last,
                end = matchResult.range.last + 1
            )
        }

        // Strikethrough: ~~text~~
        val strikeRegex = Regex("~~(.*?)~~")
        strikeRegex.findAll(text.text).forEach { matchResult ->
            builder.addStyle(
                style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.first,
                end = matchResult.range.first + 2
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.last - 1,
                end = matchResult.range.last + 1
            )
        }

        // Underline: __text__
        val underlineRegex = Regex("__(.*?)__")
        underlineRegex.findAll(text.text).forEach { matchResult ->
            builder.addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.first,
                end = matchResult.range.first + 2
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.range.last - 1,
                end = matchResult.range.last + 1
            )
        }

        // Headings: # Heading
        val headingRegex = Regex("(#{1,3})\\s+(.*)$", RegexOption.MULTILINE)
        headingRegex.findAll(text.text).forEach { matchResult ->
            builder.addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            builder.addStyle(
                style = SpanStyle(color = Color.Transparent, fontSize = 0.1.sp),
                start = matchResult.groups[1]!!.range.first,
                end = matchResult.groups[1]!!.range.last + 1
            )
        }

        // We do NOT modify the text string length, so OffsetMapping is identity.
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}
