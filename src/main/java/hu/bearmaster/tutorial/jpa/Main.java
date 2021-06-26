package hu.bearmaster.tutorial.jpa;

import static hu.bearmaster.tutorial.jpa.model.Address.address;

import hu.bearmaster.tutorial.jpa.dao.AddressDao;
import hu.bearmaster.tutorial.jpa.dao.UserDao;
import hu.bearmaster.tutorial.jpa.model.Address;
import hu.bearmaster.tutorial.jpa.model.User;

public class Main {
    
    private final UserDao userDao = new UserDao();
    private final AddressDao addressDao = new AddressDao();

    public static void main(String[] args) {
        Main main = new Main();
        main.getAddress();
    }

    private void createAddress() {
        Address address = address("Szeged", "Kossuth", 3);
        addressDao.create(address);
        System.out.println(address);
    }
    
    private void associateAddressToUser() {
        User user = userDao.getUserById(1L);
        Address address = addressDao.getAddressById(15L);
        System.out.println(address);
        
        address.setUser(user);
        addressDao.update(address);
        System.out.println(address);
    }
    
    private void associateUserToAddress() {
        User user = userDao.getUserById(1L);
        Address address = addressDao.getAddressById(15L);
        System.out.println(user);
        
        user.setAddress(address);
        addressDao.update(user.getAddress());
        System.out.println(user);
    }
    
    private void getUser() {
        User user = userDao.getUserById(1L);
        System.out.println(user);
    }
    
    private void getAddress() {
        Address address = addressDao.getAddressById(15L);
        System.out.println(address);
        System.out.println(address.getUser());
    }
    
    private void dissociateAddressFromUser() {
        Address address = addressDao.getAddressById(14L);
        System.out.println(address);
        
        address.setUser(null);
        addressDao.update(address);
        System.out.println(address);
    }
    
    private void deleteAddress() {
        Address address = addressDao.getAddressById(14L);
        System.out.println(address);
        addressDao.remove(address);
    }
    
    private void createAddressWithAssociation() {
        User user = userDao.getUserById(1L);
        Address address = address("Budapest", "Rákóczi", 15);
        address.setUser(user);
        addressDao.create(address);
        System.out.println(address);
    }

}
