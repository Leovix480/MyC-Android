package com.mycompany.mycapp

/**
 * Datos de conexion con la API de MyC.
 *
 * URL_BASE apunta al tunel publico (Cloudflare Tunnel) que corre en el servidor,
 * asi que funciona desde cualquier red, no solo en la WiFi de la panaderia.
 *
 * OJO: esta URL cambia si el servicio "myc-tunnel" del servidor se reinicia
 * (es un tunel "rapido" sin cuenta de Cloudflare, no tiene nombre fijo). Si la
 * app deja de traer datos de golpe, lo primero es pedir la URL nueva con:
 *   sudo journalctl -u myc-tunnel -n 30 --no-pager | grep trycloudflare
 * y actualizarla aca.
 *
 * CLAVE_API tiene que ser identica a la variable MYC_API_KEY del servidor
 * (esta en ~/myc-api/myc-api.env). Ojo: una clave dentro del APK se puede
 * extraer, por eso la proteccion real es que el usuario de MySQL que usa la
 * API (myc_app) solo tiene permiso SELECT.
 */
object Config {
    const val URL_BASE = "https://starsmerchant-mounts-waiver-forty.trycloudflare.com/"
    const val CLAVE_API = "gBQKdRwMrMjMfL7EohTfi95TijKPzUtw"
}
