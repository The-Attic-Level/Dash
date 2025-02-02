package com.the_attic_level.dash.ui

import android.content.Context
import android.graphics.Outline
import android.graphics.PorterDuff
import android.os.Build
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Space
import android.widget.Spinner
import android.widget.TextView
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.UIRect
import com.the_attic_level.dash.ui.layout.type.UIText
import com.the_attic_level.dash.ui.painter.icon.Icon
import com.the_attic_level.dash.ui.painter.icon.IconView
import com.the_attic_level.dash.ui.painter.shape.UIFrame
import com.the_attic_level.dash.ui.widget.drawable.ColorDrawable
import com.the_attic_level.dash.ui.widget.drawable.ShapeDrawable
import com.the_attic_level.dash.ui.widget.view.DashFrameLayout
import com.the_attic_level.dash.ui.widget.view.DashGridView
import com.the_attic_level.dash.ui.widget.view.DashHorizontalScrollView
import com.the_attic_level.dash.ui.widget.view.DashLayoutParams
import com.the_attic_level.dash.ui.widget.view.DashLinearLayout
import com.the_attic_level.dash.ui.widget.view.DashListView
import com.the_attic_level.dash.ui.widget.view.DashScrollView
import kotlin.math.max

interface UIHelper
{
    // ----------------------------------------
    // View Outlines
    
    fun createOutline(radius: Int): ViewOutlineProvider? {
        if (radius > 0) {
            return object: ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
                }
            }
        }
        return null
    }
    
    // ----------------------------------------
    // Create Views
    
    fun text(context: Context, rect: UIRect, text: UIText, textID: Int = 0) = TextView(context).also {
        UI.setup(it, rect, text)
        if (textID != 0) {
            it.setText(textID)
        }
    }
    
    fun edit(context: Context, rect: UIRect, text: UIText, textID: Int = 0) = EditText(context).also {
        UI.setup(it, rect, text)
        if (textID != 0) {
            it.setText(textID)
        }
    }
    
    fun vertical  (context: Context, rect: UIRect) = linear(context, rect, LinearLayout.VERTICAL)
    fun horizontal(context: Context, rect: UIRect) = linear(context, rect, LinearLayout.HORIZONTAL)
    
    fun linear(context: Context, rect: UIRect, orientation: Int) = DashLinearLayout(context).also {
        it.orientation = orientation
        UI.setup(it, rect)
    }
    
    fun   verticalScroll(context: Context, rect: UIRect) =           DashScrollView(context).also { UI.setup(it, rect) }
    fun horizontalScroll(context: Context, rect: UIRect) = DashHorizontalScrollView(context).also { UI.setup(it, rect) }
    
    fun view     (context: Context, rect: UIRect) = View           (context).also { UI.setup(it, rect) }
    fun list     (context: Context, rect: UIRect) = DashListView   (context).also { UI.setup(it, rect) }
    fun grid     (context: Context, rect: UIRect) = DashGridView   (context).also { UI.setup(it, rect) }
    fun frame    (context: Context, rect: UIRect) = DashFrameLayout(context).also { UI.setup(it, rect) }
    fun image    (context: Context, rect: UIRect) = ImageView      (context).also { UI.setup(it, rect) }
    fun spinner  (context: Context, rect: UIRect) = Spinner        (context).also { UI.setup(it, rect) }
    fun progress (context: Context, rect: UIRect) = ProgressBar    (context).also { UI.setup(it, rect) }
    fun web      (context: Context, rect: UIRect) = WebView        (context).also { UI.setup(it, rect) }
    
    fun progress(context: Context, rect: UIRect, color: UIR): ProgressBar
    {
        val view = progress(context, rect)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.indeterminateTintList = UIR.list(color, color)
            // view.indeterminateTintMode = PorterDuff.Mode.SRC_ATOP
        } else {
            @Suppress("DEPRECATION")
            view.indeterminateDrawable?.setColorFilter(color.color, PorterDuff.Mode.MULTIPLY)
        }
        
        return view
    }
    
    fun space(context: Context, width: Int=0, height: Int=0, weight: Float=0.0F) = Space(context).also {
        it.visibility = View.INVISIBLE
        UI.setup(it, UIRect(width=width, height=height, weight=weight))
    }
    
    fun icon(context: Context, rect: UIRect, res: UIR=UIR.WHITE) =
        IconView(context).also {
            UI.setup(it, rect)
            it.painter.res = res
        }
    
    fun icon(context: Context, rect: UIRect, icon: Icon, scale: Float=0.35F, color: UIR=UIR.WHITE) =
        IconView(context).also {
            UI.setup(it, rect)
            it.painter.icon  = icon
            it.painter.scale = scale
            it.painter.res   = color
        }
    
    fun icon(context: Context, rect: UIRect, icon: Icon, size: Int, color: UIR=UIR.WHITE) =
        IconView(context).also {
            UI.setup(it, rect)
            it.painter.icon = icon
            it.painter.size = size
            it.painter.res  = color
        }
    
    // ----------------------------------------
    // View Setup
    
    fun setup(view: View?, rect: UIRect) {
        if (view != null) {
            rect.apply(view)
        }
    }
    
    fun setup(view: TextView?, rect: UIRect, text: UIText) {
        if (view != null) {
            rect.apply(view)
            text.apply(view)
        }
    }
    
    fun setup(view: EditText?, rect: UIRect, text: UIText) {
        if (view != null) {
            rect.apply(view)
            text.apply(view)
        }
    }
    
    fun setupPasswordInput(view: EditText?) {
        view?.inputType = UIText.INPUT_PASSWORD
        view?.transformationMethod = PasswordTransformationMethod()
    }
    
    // ----------------------------------------
    // Color Setup
    
    fun background(view: View?, color: UIR, radius: Int=0, stroke: Int=0) {
        if (view != null) {
            view.background = ShapeDrawable(color, UIFrame(radius, stroke))
        }
    }
    
    fun background(view: View?, res: UIR) {
        if (view != null) {
            if (res.type == UIR.Type.DRAWABLE) {
                view.background = res.drawable
            } else {
                view.background = ColorDrawable(res)
            }
        }
    }
    
    fun textColor(view: TextView?, res: UIR) {
        if (view != null) {
            if (res.type == UIR.Type.COLOR) {
                view.setTextColor(res.color)
            } else if (res.type == UIR.Type.COLOR_LIST) {
                view.setTextColor(res.list)
            }
        }
    }
    
    fun hintColor(view: TextView?, res: UIR) {
        if (view != null) {
            if (res.type == UIR.Type.COLOR) {
                view.setHintTextColor(res.color)
            } else if (res.type == UIR.Type.COLOR_LIST) {
                view.setHintTextColor(res.list)
            }
        }
    }
    
    // ----------------------------------------
    // Selector
    
    fun <T: View> findChildByVisibility(group: ViewGroup?, cls: Class<T>, visibility: Int): T? {
        return findChild(group, cls) {
            it.visibility == visibility
        }
    }
    
    fun <T: View> findChild(group: ViewGroup?, cls: Class<T>, selector: (T) -> Boolean): T? {
        if (group != null) {
            for (i in 0 until group.childCount) {
                val child = group.getChildAt(i)
                if (cls.isInstance(child)) {
                    val view = cls.cast(child)!!
                    if (selector(view)) {
                        return cls.cast(child)
                    }
                }
            }
        }
        return null
    }
    
    // ----------------------------------------
    // Visibility
    
    fun visible(view: View?): Boolean =
        view != null && view.visibility == View.VISIBLE && view.alpha > 0.0F
    
    fun visibility(view: View?, visibility: Int) {
        if (view != null && view.visibility != visibility) {
            view.visibility = visibility
        }
    }
    
    fun childVisibility(group: ViewGroup?, visibility: Int) {
        if (group != null) {
            for (i in 0 until group.childCount) {
                group.getChildAt(i).visibility = visibility
            }
        }
    }
    
    // ----------------------------------------
    // Margin
    
    fun margin(view: View?, size: Int) {
        margin(view, size, size, size, size)
    }
    
    fun margin(view: View?, px: Int, py: Int) {
        margin(view, px, py, px, py)
    }
    
    fun margin(view: View?, left: Int=-1, top: Int=-1, right: Int=-1, bottom: Int=-1)
    {
        val params = view?.layoutParams
        
        if (params is ViewGroup.MarginLayoutParams)
        {
            if (left   >= 0) params.leftMargin   = left
            if (top    >= 0) params.topMargin    = top
            if (right  >= 0) params.rightMargin  = right
            if (bottom >= 0) params.bottomMargin = bottom
            
            view.layoutParams = params
        }
    }
    
    // ----------------------------------------
    // Padding
    
    fun padding(view: View?, size: Int) {
        padding(view, size, size, size, size)
    }
    
    fun padding(view: View?, px: Int, py: Int) {
        padding(view, px, py, px, py)
    }
    
    fun padding(view: View?, left: Int=-1, top: Int=-1, right: Int=-1, bottom: Int=-1) {
        view?.setPadding(
            max(left,   view.paddingLeft),
            max(top,    view.paddingTop),
            max(right,  view.paddingRight),
            max(bottom, view.paddingBottom))
    }
    
    // ----------------------------------------
    // Gravity / Weight
    
    fun weight(view: View?, weight: Float) {
        val params = view?.layoutParams
        if (params is LinearLayout.LayoutParams) {
            params.weight = weight
            view.layoutParams = params
        } else if (params is DashLayoutParams) {
            params.weight = weight
            // no need to apply params to view
        }
    }
    
    fun gravity(view: View?, gravity: Int) {
        val params = view?.layoutParams
        if (params is FrameLayout.LayoutParams) {
            params.gravity = gravity
            view.layoutParams = params
        } else if (params is LinearLayout.LayoutParams) {
            params.gravity = gravity
            view.layoutParams = params
        }
    }
    
    fun listViewType(view: View?, type: Int) {
        val params = view?.layoutParams
        if (params is DashLayoutParams) {
            params.viewType = type
            // no need to apply params to view
        }
    }
}