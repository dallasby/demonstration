namespace my.company;

entity Products {
    key ID    : Integer;
        name  : String;
        price : Decimal;
        stock : Integer;
}

entity Users {
    key ID   : Integer;
        name : String;
}

entity Orders {
    key ID         : Integer;
        quantity   : Integer;
        totalPrice : Decimal;
        user       : Association to Users;
        product    : Association to many Products;
}
