package com.italo.conversormoedas.util

/**
 * Sealed Class que encapsula o estado de uma operação, como uma chamada de API ou acesso ao DB.
 *
 * @param T O tipo de dado que a operação deve retornar em caso de sucesso.
 * @property data O dado em si, se a operação for bem-sucedida.
 * @property message Uma mensagem de erro, se a operação falhar.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}