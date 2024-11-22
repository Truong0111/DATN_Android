package com.truongtq_datn

data object ApiEndpoint {
    private var Url_Server = "https://192.168.0.118:3000"

    var Endpoint_Account = "$Url_Server/account"
    var Endpoint_Account_Login = "$Endpoint_Account/login"
    var Endpoint_Account_Register = "$Endpoint_Account/register"
    var Endpoint_Account_GetAll = "$Endpoint_Account/getAllAccounts"

    var Endpoint_Door = "$Url_Server/door"
    var Endpoint_Door_Create = "$Endpoint_Door/create"
    var Endpoint_Door_GetAll = "$Endpoint_Door/getAllDoors"

    var Endpoint_Ticket = "$Url_Server/ticket"
    var Endpoint_Ticket_Create = "$Url_Server/create"
    var Endpoint_Ticket_IdAccount = "$Url_Server/idAccount"
    var Endpoint_Ticket_IdDoor = "$Url_Server/idDoor"
}