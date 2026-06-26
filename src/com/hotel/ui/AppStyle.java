package com.hotel.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * AppStyle
 * ------------------------------------------------------------
 * Holds shared colors and fonts so every screen looks
 * consistent. This is NOT a design pattern for the rubric -
 * it's just a simple way to avoid typing the same color codes
 * in six different files.
 *
 * Palette (hotel brand colors):
 *   Navy      - headers, primary buttons
 *   Gold      - accents, highlights (hospitality / brass feel)
 *   Off-white - main background
 *   Green     - success / available / active
 *   Red       - danger / delete / cancel
 */
public class AppStyle {

    public static final Color NAVY        = new Color(0x2C, 0x3E, 0x50);
    public static final Color GOLD        = new Color(0xC9, 0xA5, 0x5C);
    public static final Color OFF_WHITE   = new Color(0xF7, 0xF5, 0xF2);
    public static final Color WHITE       = Color.WHITE;
    public static final Color GREEN       = new Color(0x2E, 0x7D, 0x52);
    public static final Color RED         = new Color(0xC0, 0x39, 0x2B);
    public static final Color LIGHT_GREY  = new Color(0xE3, 0xE0, 0xDA);
    public static final Color TEXT_DARK   = new Color(0x2C, 0x2C, 0x2C);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT    = new Font("Segoe UI", Font.BOLD, 24);
}
