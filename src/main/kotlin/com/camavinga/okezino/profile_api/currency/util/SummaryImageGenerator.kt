package com.camavinga.okezino.profile_api.currency.util

import com.camavinga.okezino.profile_api.currency.data.CountryOutput
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.NumberFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

object SummaryImageGenerator {

    fun generateSummaryImage(countries: List<CountryOutput>) {
        // Ensure cache directory exists
        val cacheDir: Path = Paths.get("cache")
        if (!Files.exists(cacheDir)) {
            Files.createDirectories(cacheDir)
        }

        val total = countries.size

        // Determine top 5 by estimated_gdp (treat null as 0)
        val top5 = countries.sortedByDescending { it.estimated_gdp ?: 0.0 }
            .take(5)

        // Image settings
        val width = 900
        val height = 400
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        try {
            // enable anti-aliasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.color = Color.WHITE
            g.fillRect(0, 0, width, height)

            // Title
            g.color = Color(33, 37, 41)
            val titleFont = Font("SansSerif", Font.BOLD, 28)
            g.font = titleFont
            g.drawString("Countries Summary", 30, 50)

            // Subtitle / total
            val subtitleFont = Font("SansSerif", Font.PLAIN, 18)
            g.font = subtitleFont
            g.color = Color.DARK_GRAY
            g.drawString("Total countries: $total", 30, 90)

            // Timestamp
            val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            g.drawString("Last refresh: $timestamp", 30, 115)

            // Divider
            g.color = Color.LIGHT_GRAY
            g.fillRect(30, 130, width - 60, 2)

            // Top 5 list
            val listFont = Font("SansSerif", Font.PLAIN, 16)
            g.font = listFont
            g.color = Color.BLACK

            val numberFormat = NumberFormat.getInstance()
            var y = 160
            if (top5.isEmpty()) {
                g.drawString("No countries available", 30, y)
            } else {
                top5.forEachIndexed { idx, country ->
                    val name = country.name ?: "Unknown"
                    val gdpValue = country.estimated_gdp ?: 0.0
                    val formatted = numberFormat.format(gdpValue)
                    val line = "${idx + 1}. $name — estimated GDP: $formatted"
                    g.drawString(line, 30, y)
                    y += 30
                }
            }

            // Save image
            val outFile = File(cacheDir.toFile(), "summary.png")
            ImageIO.write(image, "png", outFile)
        } finally {
            g.dispose()
        }
    }
}

