/*
 * Created in 2019, Gabor Varadi
 *
 * This file, the code contained within the file, and any parts of the code can be freely distributed or used in any purposes.
 * Any code/software derived from this file does not need to retain this notice.
 *
 * IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import kotlin.coroutines.cancellation.CancellationException

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun View.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    context.showToast(text, duration)
}

fun Context.showToast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(stringRes), duration).show()
}

fun View.showToast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    context.showToast(stringRes, duration)
}

fun Context.showLongToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun View.showLongToast(text: String) {
    context.showLongToast(text)
}

fun Context.showLongToast(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_LONG).show()
}

fun View.showLongToast(@StringRes stringRes: Int) {
    context.showLongToast(stringRes)
}

tailrec fun <T : Activity> Context.findActivity(): T {
    if (this is Activity) {
        @Suppress("UNCHECKED_CAST")
        return this as T
    } else {
        if (this is ContextWrapper) {
            return this.baseContext.findActivity()
        }
        throw IllegalStateException("The context does not contain Activity in the context chain!")
    }
}

// event handling
inline fun View.onClick(crossinline clickListener: () -> Unit): View.OnClickListener {
    val click = View.OnClickListener { _ ->
        clickListener()
    }
    setOnClickListener(click)
    return click
}

inline fun EditText.onTextChanged(crossinline textChangeListener: (String) -> Unit): TextWatcher {
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textChangeListener(s.toString())
        }
    }
    this.addTextChangedListener(textWatcher)
    return textWatcher
}

// animations + views
fun View.objectAnimate() = ViewPropertyObjectAnimator.animate(this)

private typealias OnMeasuredCallback = (view: View, width: Int, height: Int) -> Unit

inline fun View.waitForMeasure(crossinline callback: OnMeasuredCallback) {
    val view = this
    val width = view.getWidth()
    val height = view.getHeight()

    if (width > 0 && height > 0) {
        callback(view, width, height)
        return
    }

    view.getViewTreeObserver().addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val observer = view.getViewTreeObserver()
            if (observer.isAlive()) {
                observer.removeOnPreDrawListener(this)
            }

            callback(view, view.getWidth(), view.getHeight())

            return true
        }
    })
}

fun animateTogether(vararg animators: Animator) = AnimatorSet().apply {
    playTogether(*animators)
}

inline fun createAnimatorListener(
    crossinline onAnimationEnd: (Animator) -> Unit = {},
    crossinline onAnimationStart: (Animator) -> Unit = {},
    crossinline onAnimationCancel: (Animator) -> Unit = {},
    crossinline onAnimationRepeat: (Animator) -> Unit = {}
) = object : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator) = onAnimationRepeat(animation)

    override fun onAnimationEnd(animation: Animator) = onAnimationEnd(animation)

    override fun onAnimationCancel(animation: Animator) = onAnimationCancel(animation)

    override fun onAnimationStart(animation: Animator) = onAnimationStart(animation)
}

inline fun AnimatorSet.onAnimationEnd(crossinline onAnimationEnd: (Animator) -> Unit): AnimatorSet =
    apply {
        addListener(createAnimatorListener(onAnimationEnd = onAnimationEnd))
    }

fun View.animateFadeOut(duration: Long = 325): Animator = run {
    alpha = 1f
    objectAnimate()
        .alpha(0f)
        .setDuration(duration)
        .get()
}

fun View.animateFadeIn(duration: Long = 325): Animator = run {
    alpha = 0f
    objectAnimate()
        .alpha(1f)
        .setDuration(duration)
        .get()
}

private fun View.animateTranslateXBy(from: Int, by: Int, duration: Long = 325): Animator = run {
    translationX = from.toFloat()
    objectAnimate()
        .translationXBy(by.toFloat())
        .setDuration(duration)
        .get()
}

fun View.animateTranslateIn(width: Int, direction: Int, duration: Long = 325): Animator =
    animateTranslateXBy(
        from = direction * width,
        by = (-1) * direction * width,
        duration = duration
    )

fun View.animateTranslateOut(width: Int, direction: Int, duration: Long = 325): Animator =
    animateTranslateXBy(
        from = 0,
        by = (-1) * direction * width,
        duration = duration
    )

inline fun <T : View> T.showIf(predicate: (T) -> Boolean): T = this.apply {
    if (predicate(this)) {
        show()
    } else {
        hide()
    }
    return this
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

// SharedPref helpers
inline fun SharedPreferences.save(actions: SharedPreferences.Editor.() -> Unit) {
    this.save(false, actions)
}

@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.save(
    immediate: Boolean,
    actions: SharedPreferences.Editor.() -> Unit
) {
    this.edit().apply(actions).let { editor ->
        if (immediate) {
            editor.commit()
        } else {
            editor.apply()
        }
    }
}

inline fun <T : Any> tryOrNull(block: () -> T): T? = try {
    block()
} catch (e: Throwable) {
    if (e is CancellationException) { // re-throw
        throw e
    }
    null
}