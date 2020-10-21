package com.mustafa.tandem

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.mustafa.tandem.util.TandemTestRunner]
 */

class TestTandemApp : Application()