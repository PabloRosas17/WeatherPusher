package weather.pusher.util

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

/* @desc contract for views capable of binding, all views in this case since mvvm architecture
* @param T type of the calling class */
interface ViewBinderIf<T> {

    /* T type of the calling class, associate the binding element */
    var mBinding: T

    /* method used to enforce bindings happen */
    fun fireUiBindings()

    /* method used to enforce ui elements who need extra help outside of the mvvm layouts */
    fun subscribeUi()
}