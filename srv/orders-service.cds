using {my.company as company} from '../db/schema';
using {AdminService as admin} from './admin-service';

service OrdersService @(requires: 'authenticated-user') {
    entity Orders as projection on company.Orders;

    @requires: 'authenticated-user'
    action   createOrder(ID : Integer, quantity : Integer, totalPrice : Decimal, user : admin.Users:ID, product : admin.Products:ID);

    @requires: 'authenticated-user'
    action   deleteOrder(ID : Orders:ID);

    @requires: 'authenticated-user'
    function getOrdersByUser(user : admin.Users:ID) returns array of Orders;
}
