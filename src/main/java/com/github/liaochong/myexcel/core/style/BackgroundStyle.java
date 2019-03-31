/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.liaochong.myexcel.core.style;

import com.github.liaochong.myexcel.core.cache.WeakCache;
import com.github.liaochong.myexcel.utils.ColorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * @author liaochong
 * @version 1.0
 */
@Slf4j
public final class BackgroundStyle {

    public static final String BACKGROUND_COLOR = "background-color";

    private static final WeakCache<String, Color> CACHE = new WeakCache<>();

    public static void setBackgroundColor(CellStyle style, Map<String, String> tdStyle, CustomColor customColor) {
        if (Objects.isNull(tdStyle)) {
            return;
        }
        String color = tdStyle.get(BACKGROUND_COLOR);
        if (Objects.isNull(color)) {
            return;
        }
        Short colorPredefined = ColorUtil.getPredefinedColorIndex(color);
        if (Objects.nonNull(colorPredefined)) {
            style.setFillForegroundColor(colorPredefined);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return;
        }
        int[] rgb = ColorUtil.getRGBByColor(color);
        setCustomColor(style, rgb, customColor);
    }

    private static void setCustomColor(CellStyle style, int[] rgb, CustomColor customColor) {
        if (Objects.isNull(rgb)) {
            return;
        }
        if (customColor.isXls()) {
            log.warn("The. XLS file does not support custom background colors for the time being. Use predefined colors please");
        } else {
            XSSFCellStyle xssfCellStyle = (XSSFCellStyle) style;
            String rgbFormat = formatRGB(rgb);
            Color color = CACHE.get(rgbFormat);
            if (Objects.isNull(color)) {
                color = new Color(rgb[0], rgb[1], rgb[2]);
                CACHE.cache(rgbFormat, color);
            }
            xssfCellStyle.setFillForegroundColor(new XSSFColor(color, customColor.getDefaultIndexedColorMap()));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }

    private static String formatRGB(int[] rgb) {
        return rgb[0] + "_" + rgb[1] + "_" + rgb[2];
    }
}
