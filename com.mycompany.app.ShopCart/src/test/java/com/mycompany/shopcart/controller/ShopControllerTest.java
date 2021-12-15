package com.mycompany.shopcart.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ProductView;

public class ShopControllerTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private ShopController shopController;
	
	@Mock
	private ProductView productView;
	
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
		shopController.allStudents();
		verify(productView).showAllProducts(products);
	}

}
