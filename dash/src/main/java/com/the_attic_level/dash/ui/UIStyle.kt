package com.the_attic_level.dash.ui

import android.graphics.Color
import android.view.Gravity
import com.the_attic_level.dash.app.DashApp
import com.the_attic_level.dash.app.ternary
import com.the_attic_level.dash.ui.layout.font.UIFont
import com.the_attic_level.dash.ui.layout.res.UIColorList
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.UIPadding
import com.the_attic_level.dash.ui.layout.type.UIText
import com.the_attic_level.dash.ui.window.DashWindow
import com.the_attic_level.dash.ui.window.WindowAnimator
import com.the_attic_level.dash.ui.window.WindowBackground
import com.the_attic_level.dash.ui.window.dialog.DashDialog
import com.the_attic_level.dash.ui.window.dropdown.DashDropdown
import com.the_attic_level.dash.ui.window.overlay.DashOverlay
import com.the_attic_level.dash.ui.window.sheet.BottomSheetAnimator
import com.the_attic_level.dash.ui.window.sheet.BottomSheetBackground
import com.the_attic_level.dash.ui.window.sheet.DashBottomSheet

open class UIStyle
{
    // ----------------------------------------
    // Static
    
    companion object {
        val shared: UIStyle
            get() = DashApp.shared.style
    }
    
    // ----------------------------------------
    // Properties
    
    open val dialog by lazy {
        Dialog()
    }
    
    open val dropdown by lazy {
        Dropdown()
    }
    
    open val bottomSheet by lazy {
        BottomSheet()
    }
    
    // ----------------------------------------
    // Dialog
    
    open class Dialog(
        
        // window style
        
        override val dimAmount: Float =
            DashOverlay.DEFAULT_ALPHA,
        
        override val panelWidth: Int =
            UI.even(ternary(UI.isLargeTablet, 980, 880)),
        
        override val animator: WindowAnimator =
            WindowAnimator(),
        
        override val background: WindowBackground = WindowBackground(
            shadowSize  = UI.scale(32),
            shadowColor = 64,
            panelRadius = UI.scale(48),
            panelColor  = Color.WHITE,
            useOutlines = false),
        
        // dialog style
        
        override val progressWheelSize : Int = UI.scale(120),
        override val minButtonWidth    : Int = UI.scale(200),
        
        override val title: UIText = UIText(
            font    = UIFont.SYSTEM_BOLD,
            size    = UI.scale(46),
            color   = UIR.hsv(195, 0.4F, 0.3F),
            gravity = Gravity.START,
            padding = UIPadding.scaled(60, 60, 60, 30)),
        
        override val text: UIText = UIText(
            font    = UIFont.SYSTEM_NORMAL,
            size    = UI.scale(42),
            color   = UIR.hsv(195, 0.4F, 0.3F),
            gravity = Gravity.START,
            padding = UIPadding.scaled(60, 30)),
        
        override val button: UIText = UIText(
            font  = UIFont.SYSTEM_BOLD,
            size  = UI.scale(38),
            color = UIColorList(
                disabled = UIR.rgb(175, 180, 185),
                enabled  = UIR.hsv(195, 0.5F, 0.35F),
                pressed  = UIR.hsv(195, 1.0F, 0.78F)),
            gravity = Gravity.CENTER,
            padding = UIPadding.scaled(50, 60))
        
    ): DashDialog.Style
    
    // ----------------------------------------
    // Dropdown
    
    open class Dropdown(
        
        // window style
        
        override var dimAmount: Float = 0.0F,
        
        override var panelWidth: Int =
            UI.even(ternary(UI.isLargeTablet, 780, 700)),
        
        override var animator: DashWindow.Animator =
            WindowAnimator(),
        
        override var background: DashWindow.Background = WindowBackground(
            shadowSize  = UI.scale(32),
            shadowColor = 32,
            panelRadius = UI.scale(32),
            panelColor  = Color.WHITE,
            useOutlines = true),
        
        // dropdown style
        
        override var dividerColor  : UIR = UIR.rgb(225, 230, 235),
        override var dividerHeight : Int = UI.scale(2),
        
        override var iconColor : UIR = UIR.rgb(160),
        override var iconSize  : Int = UI.even(36),
        override var iconSpace : Int = UI.scale(160),
        
        override val minEntryHeight: Int = UI.scale(200),
        
        override val popupTitle: UIText = UIText(
            font    = UIFont.SYSTEM_BOLD,
            size    = UI.scale(40),
            color   = UIR.hsv(195, 0.4F, 0.3F),
            gravity = UI.LEFT_CENTER,
            padding = UIPadding.scaled(px = 36, py = 32)),
        
        override val entryTitle: UIText = UIText(
            font    = UIFont.SYSTEM_NORMAL,
            size    = UI.scale(45),
            color   = UIR.hsv(195, 0.4F, 0.3F),
            gravity = UI.LEFT_CENTER,
            padding = UIPadding.scaled(px=36, py=52))
        
    ): DashDropdown.Style
    
    // ----------------------------------------
    // Bottom Sheet
    
    open class BottomSheet(
        override var dimAmount  : Float = DashOverlay.DEFAULT_ALPHA,
        override var animator   : DashWindow.Animator   = BottomSheetAnimator(),
        override val background : DashWindow.Background = BottomSheetBackground(false)
    ): DashBottomSheet.Style
}