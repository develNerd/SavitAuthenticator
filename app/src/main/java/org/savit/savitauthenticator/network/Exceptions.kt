package org.savit.savitauthenticator.network

import java.io.IOException
import java.lang.IllegalStateException

class ApiException(message: String) : IOException(message)
class NoInternetException(message: String) : IOException(message)
class APIERROR(message: String):IllegalStateException(message)
