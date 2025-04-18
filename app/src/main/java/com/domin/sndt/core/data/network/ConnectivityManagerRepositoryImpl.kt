package com.domin.sndt.core.data.network

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellSignalStrengthCdma
import android.telephony.CellSignalStrengthGsm
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import android.telephony.CellSignalStrengthTdscdma
import android.telephony.CellSignalStrengthWcdma
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.domin.sndt.core.data.api.IpifyRepositoryImpl
import com.domin.sndt.core.domain.repo.NetworkInterfaceRepository
import com.domin.sndt.core.domain.repo.ConnectivityManagerRepository
import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.CellDetails
import com.domin.sndt.info.ConnectionInfo
import com.domin.sndt.info.WifiDetails
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.net.Inet4Address
import java.net.Inet6Address

class ConnectivityManagerRepositoryImpl(
    private val context: Application,
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager,
    private val telephonyManager: TelephonyManager,
    private val networkInterfaceRepository: NetworkInterfaceRepository,
    private val ipifyRepositoryImpl: IpifyRepositoryImpl
): ConnectivityManagerRepository {

    override suspend fun networkCallback(callback: (Network?) -> Unit) {
        val networkListener = object: NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.i("CMRI","onLost Wifi")
                callback(null)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities?.let {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("CMRI","onAvailable Wifi")
                        callback(network)
                    }
                }
            }
        }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            networkListener
        )
    }

    override suspend fun getWifiDetails(): WifiDetails {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)!!
        val isWifiEnabled = wifiManager.isWifiEnabled
        val connectionState = "Connected"
        val ssid: String?
        val bssid: String?
        val channel: String?
        val speed: String?
        val signalStrength: String?
        var dhcpLease: Int? = null

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            dhcpLease = wifiManager.dhcpInfo.leaseDuration
            ssid = wifiManager.connectionInfo.ssid
            bssid = wifiManager.connectionInfo.bssid
            speed = wifiManager.connectionInfo.linkSpeed.toString()
            signalStrength = wifiManager.connectionInfo.rssi.toString()
            channel = wifiManager.connectionInfo.frequency.toString()
        } else {
            val wifiInfo = networkCapabilities.transportInfo as (WifiInfo)
            ssid = wifiInfo.ssid
            bssid = wifiInfo.bssid
            speed = wifiInfo.linkSpeed.toString()
            signalStrength = wifiInfo.rssi.toString()
            channel = wifiInfo.frequency.toString()
        }

        return WifiDetails(isWifiEnabled,connectionState,dhcpLease,ssid,bssid,channel,speed,signalStrength)
    }

    override suspend fun getCellDetails(): CellDetails {
        val dataState = telephonyManager.dataState // TelephonyManager.DATA_$ for the string
        val dataActivity = telephonyManager.dataActivity // TelephonyManager.DATA_ACTIVITY_$ for the string
        val roaming = telephonyManager.isNetworkRoaming
        val simState = telephonyManager.simState // TelephonyManager.SIM_STATE_$ for the string
        val simName = telephonyManager.simOperatorName
        val simMccMnc = telephonyManager.simOperator
        val operatorName = telephonyManager.networkOperatorName

        val networkType = if (
            context.checkSelfPermission(
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            telephonyManager.dataNetworkType
        } else {
            null
        }

        val phoneType = telephonyManager.phoneType

        return CellDetails(dataState,dataActivity,roaming,simState,simName,simMccMnc,operatorName,networkType,phoneType)
    }

    override suspend fun getCellSignalStrength(callback: (Int?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val telephonyCallback = object: TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    var dbm: Int? = null
                    val signalStrengthList = signalStrength.cellSignalStrengths

                    for (signal in signalStrengthList) {
                        when (signal) {
                            is CellSignalStrengthGsm -> dbm = signal.dbm
                            is CellSignalStrengthCdma -> dbm = signal.dbm
                            is CellSignalStrengthLte -> dbm = signal.dbm
                            is CellSignalStrengthWcdma -> dbm = signal.dbm
                            is CellSignalStrengthNr -> dbm = signal.dbm
                            is CellSignalStrengthTdscdma -> dbm = signal.dbm
                        }
                    }
                    callback(dbm)

                    telephonyManager.unregisterTelephonyCallback(this)
                }
            }

            try {
                telephonyManager.registerTelephonyCallback(context.mainExecutor, telephonyCallback)
            } catch (securityException: SecurityException) {
                securityException.printStackTrace()
                callback(null)
            }
        } else {
            val phoneStateListener = object: PhoneStateListener() {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                    super.onSignalStrengthsChanged(signalStrength)
                    if (signalStrength != null) {
                        var dbm: Int? = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val signalStrengthList = signalStrength.cellSignalStrengths

                            for (signal in signalStrengthList) {
                                when (signal) {
                                    is CellSignalStrengthGsm -> dbm = signal.dbm
                                    is CellSignalStrengthCdma -> dbm = signal.dbm
                                    is CellSignalStrengthLte -> dbm = signal.dbm
                                    is CellSignalStrengthWcdma -> dbm = signal.dbm
                                    is CellSignalStrengthNr -> dbm = signal.dbm
                                    is CellSignalStrengthTdscdma -> dbm = signal.dbm
                                }
                            }
                            callback(dbm)
                        } else {
                            val method = SignalStrength::class.java.getMethod("getDbm")
                            val dbm = method.invoke(signalStrength) as Int
                            callback(dbm)
                        }
                    } else {
                        callback(null)
                    }

                    telephonyManager.listen(this, LISTEN_NONE)
                }
            }

            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        }
    }

    override suspend fun getCellDataState(): Flow<Int> = callbackFlow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val dataStateCallback = object : TelephonyCallback(), TelephonyCallback.DataConnectionStateListener {
                override fun onDataConnectionStateChanged(
                    state: Int,
                    networkType: Int
                ) {
                    trySend(state)
                }
            }

            telephonyManager.registerTelephonyCallback(context.mainExecutor,dataStateCallback)

            awaitClose {
                telephonyManager.unregisterTelephonyCallback(dataStateCallback)
            }
        } else {
            val phoneStateListener = object : PhoneStateListener() {

                override fun onDataConnectionStateChanged(
                    state: Int,
                    networkType: Int
                ) {
                    super.onDataConnectionStateChanged(state, networkType)
                    trySend(state)
                }
            }

            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE)

            awaitClose {
                telephonyManager.listen(
                    phoneStateListener,
                    PhoneStateListener.LISTEN_NONE
                )
            }
        }
    }

    private suspend fun getConnectionInfo(network: Network): ConnectionInfo {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val linkProperties = connectivityManager.getLinkProperties(network)!!
        var gatewayIpv4: String? = null
        var gatewayIpv6: String? = null
        var dnsIpv4: String? = null
        var dnsIpv6: String? = null
        var subnetMask: String? = null

        for (route in linkProperties.routes) {
            val gateway = route.gateway
            if (gateway != null && route.isDefaultRoute && !gateway.isLoopbackAddress) {
                if (gateway is Inet4Address)
                    gatewayIpv4 = route.gateway?.hostAddress
                if (gateway is Inet6Address)
                    gatewayIpv6 = route.gateway?.hostAddress
            }
        }

        for (dns in linkProperties.dnsServers) {
            if (dns is Inet4Address && !dns.isLoopbackAddress)
                dnsIpv4 = dns.hostAddress
            if (dns is Inet6Address && !dns.isLoopbackAddress)
                dnsIpv6 = dns.hostAddress
        }

        networkCapabilities?.let {
            subnetMask = networkInterfaceRepository.getSubnet()
        }

        val ipv4Address = networkInterfaceRepository.getLocalIp() ?: "N/A"
        val ipv6Address = networkInterfaceRepository.getIpv6() ?: "N/A"

        return ConnectionInfo(ipv4Address,subnetMask,gatewayIpv4,dnsIpv4,ipv6Address,gatewayIpv6,dnsIpv6)
    }

    override suspend fun getConnectionDetails(network: Network): Pair<ActiveConnection,ConnectionInfo>? {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        networkCapabilities?.let {
            var publicIpv4: String? = null
            var publicIpv6: String? = null
            var httpProxy: String? = null

            val linkProperties = connectivityManager.getLinkProperties(network)
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                publicIpv4 = ipifyRepositoryImpl.getPublicIpv4()
                publicIpv6 = ipifyRepositoryImpl.getPublicIpv6()
                httpProxy = linkProperties?.httpProxy?.host
            }

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val activeConnection = ActiveConnection("Wi-Fi",publicIpv4,publicIpv6,httpProxy)
                val connectionInfo = getConnectionInfo(network)
                return Pair(activeConnection,connectionInfo)
            }
        }

        return null
    }
}