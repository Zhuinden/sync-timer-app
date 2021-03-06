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

import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.OutsideLifecycleException
import com.trello.rxlifecycle3.RxLifecycle
import com.zhuinden.simplestack.ScopedServices
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject

abstract class RxScopedService : ScopedServices.Activated, ScopedServices.Registered,
    LifecycleProvider<RxScopedService.ServiceEvent> {

    enum class ServiceEvent {
        REGISTERED,
        ACTIVATED,
        INACTIVATED,
        UNREGISTERED
    }

    private val lifecycleSubject = BehaviorSubject.create<ServiceEvent>()

    @CheckResult
    override fun lifecycle(): Observable<ServiceEvent> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    override fun <T> bindUntilEvent(event: ServiceEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    // Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities
    companion object {
        private val SERVICE_LIFECYCLE =
            Function<ServiceEvent, ServiceEvent> { lastEvent ->
                when (lastEvent) {
                    ServiceEvent.REGISTERED -> ServiceEvent.ACTIVATED
                    ServiceEvent.ACTIVATED -> ServiceEvent.INACTIVATED
                    ServiceEvent.INACTIVATED -> ServiceEvent.UNREGISTERED
                    ServiceEvent.UNREGISTERED -> throw OutsideLifecycleException("Cannot bind to lifecycle when outside of it.")
                    else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
                }
            }
    }

    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycle.bind(lifecycle(), SERVICE_LIFECYCLE)
    }

    @CallSuper
    override fun onServiceRegistered() {
        lifecycleSubject.onNext(ServiceEvent.REGISTERED)
    }

    @CallSuper
    override fun onServiceActive() {
        lifecycleSubject.onNext(ServiceEvent.ACTIVATED)
    }

    @CallSuper
    override fun onServiceInactive() {
        lifecycleSubject.onNext(ServiceEvent.INACTIVATED)
    }

    @CallSuper
    override fun onServiceUnregistered() {
        lifecycleSubject.onNext(ServiceEvent.UNREGISTERED)
    }
}

fun <T> Observable<T>.bindToRegistration(service: RxScopedService): Observable<T> =
    this.compose(RxLifecycle.bindUntilEvent(service.lifecycle(), RxScopedService.ServiceEvent.UNREGISTERED))

fun <T> Observable<T>.bindToActivation(service: RxScopedService): Observable<T> =
    this.compose(RxLifecycle.bindUntilEvent(service.lifecycle(), RxScopedService.ServiceEvent.INACTIVATED))

fun <T> Single<T>.bindToRegistration(service: RxScopedService): Single<T> =
    this.compose(RxLifecycle.bindUntilEvent(service.lifecycle(), RxScopedService.ServiceEvent.UNREGISTERED))

fun <T> Single<T>.bindToActivation(service: RxScopedService): Single<T> =
    this.compose(RxLifecycle.bindUntilEvent(service.lifecycle(), RxScopedService.ServiceEvent.INACTIVATED))