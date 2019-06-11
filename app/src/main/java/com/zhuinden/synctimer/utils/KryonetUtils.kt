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

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server

interface KryonetListener {
    fun connected(connection: Connection)
    fun disconnected(connection: Connection)
    fun received(connection: Connection, command: Any)
    fun idle(connection: Connection)
}

fun Server.addListener(kryonetListener: KryonetListener): Listener {
    val listener = object : Listener() {
        override fun connected(connection: Connection) {
            super.connected(connection)
            kryonetListener.connected(connection)
        }

        override fun disconnected(connection: Connection) {
            super.disconnected(connection)
            kryonetListener.disconnected(connection)
        }

        override fun received(connection: Connection, command: Any) {
            super.received(connection, command)
            kryonetListener.received(connection, command)
        }

        override fun idle(connection: Connection) {
            super.idle(connection)
            kryonetListener.idle(connection)
        }
    }
    this.addListener(listener)
    return listener
}

fun Client.addListener(kryonetListener: KryonetListener): Listener {
    val listener = object : Listener() {
        override fun connected(connection: Connection) {
            super.connected(connection)
            kryonetListener.connected(connection)
        }

        override fun disconnected(connection: Connection) {
            super.disconnected(connection)
            kryonetListener.disconnected(connection)
        }

        override fun received(connection: Connection, command: Any) {
            super.received(connection, command)
            kryonetListener.received(connection, command)
        }

        override fun idle(connection: Connection) {
            super.idle(connection)
            kryonetListener.idle(connection)
        }
    }
    this.addListener(listener)
    return listener
}

// kryo helpers
inline fun <reified T> Kryo.register() {
    this.register(T::class.java)
}
