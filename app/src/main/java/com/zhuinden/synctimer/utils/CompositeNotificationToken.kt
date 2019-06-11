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

import com.zhuinden.eventemitter.EventSource
import java.util.*


class CompositeNotificationToken : EventSource.NotificationToken {
    private val threadId = Thread.currentThread().id

    private val notificationTokens: LinkedList<EventSource.NotificationToken> = LinkedList()

    @Suppress("MemberVisibilityCanBePrivate")
    fun add(notificationToken: EventSource.NotificationToken) {
        notificationTokens.add(notificationToken)
    }

    private var isDisposing = false

    override fun stopListening() {
        if (threadId != Thread.currentThread().id) {
            throw IllegalStateException("Cannot stop listening on a different thread where it was created")
        }
        if (isDisposing) {
            return
        }
        isDisposing = true
        val size = notificationTokens.size
        for (i in size - 1 downTo 0) {
            val token = notificationTokens.removeAt(i)
            token.stopListening()
        }
        isDisposing = false
    }

    operator fun plusAssign(notificationToken: EventSource.NotificationToken) {
        add(notificationToken)
    }
}