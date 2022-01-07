package com.mycompany.shopcart.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ProductView;

public class ShopControllerTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private ProductView productView;
	
	@InjectMocks
	private ShopController shopController;
	
	private AutoCloseable closeable;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@After
	public void releaseMocks() throws Exception {
        closeable.close();
    }

	@Test
	public void testAllProducts () {
		List<Product> products = asList(new Product());
		when(productRepository.findAll())
			.thenReturn(products);
		shopController.allProducts();
		verify(productView).showAllProducts(products);
	}
	
	@Test
	public void testBuyProductWhenProductIsInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(productToBuy);
		shopController.buyProduct(productToBuy);
		verify(productView).addProductToCart(productToBuy);

	}
	
	@Test
	public void testBuyProductWhenProductIsNotInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(null);
		shopController.buyProduct(productToBuy);
		verify(productView).showError("The product you are trying to buy is no longer available: test");
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}
	
	@Test
	public void testRemoveProduct() {
		Product productToRemove = new Product("1", "test");
		shopController.removeProduct(productToRemove);
		verify(productView).removeProductFromCart(productToRemove);
	}
	
	@Test
	public void testCheckoutProductsWhenOneProductIsInDatabase() {
		Product product = new Product("1", "test1");
		List<Product> productsInCart = asList(product);
		when(productRepository.findById("1")).thenReturn(product);
		shopController.checkoutProducts(productsInCart);
		InOrder inOrder = inOrder(productRepository, productView);
		inOrder.verify(productRepository).delete("1");
		inOrder.verify(productView).removeProductFromCart(product);
		inOrder.verify(productView).removeProductsFromShop(product);
	}
	
	@Test
	public void testCheckoutProductsWhenSeveralProductAreInDatabase() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		List<Product> productsInCart = asList(product1, product2);
		when(productRepository.findById("1")).thenReturn(product1);
		when(productRepository.findById("2")).thenReturn(product2);
		shopController.checkoutProducts(productsInCart);
		InOrder inOrder = inOrder(productRepository, productView);
		inOrder.verify(productRepository).delete("1");
		inOrder.verify(productView).removeProductFromCart(product1);
		inOrder.verify(productView).removeProductsFromShop(product1);
		inOrder.verify(productRepository).delete("2");
		inOrder.verify(productView).removeProductFromCart(product2);
		inOrder.verify(productView).removeProductsFromShop(product2);
	}
	
	@Test
	public void testCheckoutProductsWhenAtLeastOneProductIsNotInDatabase() {
		Product product = new Product("1", "test");
		List<Product> productsInCart = asList(product);
		when(productRepository.findById("1")).thenReturn(null);
		shopController.checkoutProducts(productsInCart);
		verify(productView).showError("The product you are trying to buy is no longer available: test");
		verify(productView).removeProductFromCart(product);
		verify(productView).removeProductsFromShop(product);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

}
