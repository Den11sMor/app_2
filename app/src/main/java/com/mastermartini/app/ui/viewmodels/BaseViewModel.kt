package com.mastermartini.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mastermartini.app.MasterMartiniApplication

// Inyección manual: una sola instancia de cada repositorio para toda la app.
open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val app = application as MasterMartiniApplication
}
