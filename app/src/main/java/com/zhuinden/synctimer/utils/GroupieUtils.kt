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

import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.extensions.LayoutContainer

typealias GroupieAdapter = GroupAdapter<GroupieViewHolder>

open class GroupieViewHolder(override val containerView: View) : ViewHolder(containerView), LayoutContainer

abstract class GroupieItem : Item<GroupieViewHolder>() {
    override fun createViewHolder(itemView: View): GroupieViewHolder =
        GroupieViewHolder(itemView)
}

fun GroupAdapter<GroupieViewHolder>.replaceItemsWith(builder: MutableList<GroupieItem>.() -> Unit) {
    val list: MutableList<GroupieItem> = mutableListOf()
    list.apply(builder)
    this.update(list)
}