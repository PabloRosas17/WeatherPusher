package weather.pusher.views

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.view_account_layout_dx.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import weather.pusher.R
import weather.pusher.controllers.ViewModelAccount
import weather.pusher.databinding.ViewAccountLayoutDxBinding
import weather.pusher.views.presenters.PresenterAccount
import weather.pusher.util.BtmNavIf
import weather.pusher.util.ViewBinderIf

/* @desc activity that displays ui elements used to guide user through the calendar generation flow */
class ViewAccount: AppCompatActivity(), BtmNavIf, ViewBinderIf<ViewAccountLayoutDxBinding> {

    /* view model by koin and dependency injection by constructor */
    val mVmAccounts : ViewModelAccount by viewModel()

    /* binding type */
    override lateinit var mBinding: ViewAccountLayoutDxBinding

    /* bottom navigation view object */
    override lateinit var mBtmNavView: BottomNavigationView

    /*
     * activity lifecycle onCreate generates the Settings interface with viewmodel
     *  and produces listeners with functionality
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.fireUiBindings()
        this.registerUi()
        this.listenOnUiComponents()
    }

    /* ui will bind itself to layouts, for consistency */
    override fun fireUiBindings(){
        /* binding is generated through BR class, this will set the view through binding */
        this.mBinding = DataBindingUtil.setContentView(this, R.layout.view_account_layout_dx)
        /* bind presenter account instance with this view */
        mBinding.mPresenter = PresenterAccount(this, mVmAccounts)
        /* execute pending bindings */
        mBinding.executePendingBindings()
    }

    /* subscriber methods */
    override fun subscribeUi() {
        /* none for this class */
    }

    /* registration for ui elements */
    override fun registerUi() {
        this.mBtmNavView = this.mBinding.btmNavViewAccount
        mBtmNavView.setOnNavigationItemSelectedListener(this)
    }

    /* elements that require listeners */
    private fun listenOnUiComponents() {
        material_slider_min_temperature.isEnabled = false
        material_slider_max_temperature.isEnabled = false

        material_slider_min_temperature.valueFrom = 0f
        material_slider_min_temperature.valueTo = 100f
        material_slider_min_temperature.addOnChangeListener { slider, value, fromUser ->
            // TODO: material_slider_min_temperature.addOnChangeListener
        }
        material_slider_max_temperature.valueFrom = 0f
        material_slider_max_temperature.valueTo = 100f
        material_slider_max_temperature.addOnChangeListener { slider, value, fromUser ->
            // TODO: material_slider_max_temperature.addOnChangeListener
        }
    }

    /* handle navigation items */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_menu_account -> { }
            R.id.action_menu_settings -> {
                val mSettingsIntent = Intent(this,ViewSettings::class.java)
                this.startActivity(mSettingsIntent)
                this.finish()
            }
        }
        return true
    }
}