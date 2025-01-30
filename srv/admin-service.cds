using {my.company as company} from '../db/schema';

service AdminService @(requires: 'authenticated-user') {
    entity Products as projection on company.Products;
    entity Users    as projection on company.Users;
    entity Orders   as projection on company.Orders;
}
