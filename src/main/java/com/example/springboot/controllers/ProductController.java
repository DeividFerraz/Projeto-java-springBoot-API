package com.example.springboot.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.ProductRecordDto;
import com.example.springboot.modelo.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController /*usada para dizer q essa classe é um restFull e aceita endPoints*/
public class ProductController {

	@Autowired
	private ProductRepository productRepository;
	
	//METODOS CRUD
	
	
	//o ResponseEntity é o retorno do metodo criado. 
	/*O metodo saveProduct recebe como corpo da sua requisição os 
	 * valores no BODY, por isso a anotation @RequestBody* e o q ele 
	 * vai receber é o ProductRecordDto, que eu criei no pacote 		record.
	 * e o @Valid é para validar os valores q eu coloquei na 		requisição da classe record*/
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productDto){
		/*Ao receber um ProductRecordDto estou iniciando ele na 			linha "var productModel = new ProductModel();"
		 * */
		var productModel = new ProductModel();
		/*Seguindo ao receber os dados DTO e iniciado, agr vamos 			converter o DTO para 
		 * productModel, e para isso podemos usar um recurso do 			Spring, que é o
		 * "BeanUtils.copyProperties" que recebe (O q sera c			onvertido, tipo convertido)*/
		BeanUtils.copyProperties(productDto, productModel);
		/*Inicio esse responseEntity que é o retorno de ProductModel
		 *onde é construido em 2 partes, o stts retorno da operação, 			e o 
		 *que foi add é add no body ao salvar 
		 *Ai no Body usamos o Repository que é o que faz acesso ao 			banco com JPA
		 *eu coloco a estenção ".save" e passo o que quero salvar no 			banco no campo de valor()
		 *no caso resolvi salvar o porductModel*/
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}
	
	
	
	/*O retorno dele Vai ser um responseEntity, porem no corpo dele
	 * vai ter uma lista de produtcModel, não tem nenhuma entrada,
	 * a não ser a da propria requisição get"products" e retorna
	 * um ResponseEntity com Status 200/ok e no Body retorna todos
	 * todos os produtos usando o "productRepository" para acessar
	 * a classe e o "findAll() para buscar todos os objetos 
	 * dessa requisição"*/
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> productlist = productRepository.findAll();
		if(!productlist.isEmpty()) {
			/*Estou dizendo q para cada obj dentro de produto, mas da lista 
			 * de produto, estou obtendo ele com "UUID id = product.getIdProduct()";, Vamo Utilizar em seguida o .add que é do metodo 
			 * "org.springframework.hateoas", para add um link a cada id 
			 * existente da lista de obj product*/
			for(ProductModel product : productlist) {
				UUID id = product.getIdProduct();
	
				/*LinkTo = "diz para qual metodo vou direcionar o cliente
				 * ao clicar no link
				 * methodOn = "Diz qual controler esta o metodo, ou 
				 * qual vai ser o metodo caso tennha outr q vai receber
				 * esse direcionamento""
				 * dps dele, colocamos para onde queremo direcionar ele com
				 * .getOneProduct(id) q é o get dbaixo, e no valor dele 
				 * vai ID por que é o valor do metodo GetMapping q esta
				 * embaixo
				 * withSelfRel = "serve para mostrar o q vai ser 
				 * rediorecionado para cada um"*/
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productlist);
	}
	
	
	/*como podemos ter 2 tipos de retorno eu usei o 
	 * ResponseEntity<Object> dentro do metodo "getOneProduct"
	 * utilizamos "@PathVariable" para dizer q o valor agr vai no path e n no body,
	 * e tambem para dar o nome da variavel que estamos passando no caso
	 * essa aqui /{id} ai demos o nome de ID e logo apos o tipo do Id q é UUID id
	 * 
	 * Feito isso vamos na base de dados acessando com "productRepository" 
	 * e usamos mais um dos metodos do JPA que é o metodo FindById que serve 
	 * para acessar um ID especifico. 
	 * 
	 * fazemos um consulta na base de dados com FindById acessando ataves 
	 * do repositoy, se n existir ou seja vir vazio, deixo uma mensagem, se vier 
	 * cheio deixo outra*/
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
		//Optional por que o valor pode ou nao existir
		Optional<ProductModel> product0 = productRepository.findById(id);
		if(product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found. ");
		}
		/*como estamos utilizando na base um "Optional", ele só me retorna 
		 * o ID encontrado se eu usar o "product0.get()"*/
		product0.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products of list"));
		return ResponseEntity.status(HttpStatus.OK).body(product0.get());
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto productDto){
	Optional<ProductModel>product0 = productRepository.findById(id);
	if(product0.isEmpty()) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found. ");
		}
		/*Ao fazer o mesmo esquema do Get, ao add a atualização,
		 * nós n instanciamos um novo obj, nós pegamos o mesmo q usamos por
		 * exemplo o "product0" para atualizar, dps de ver se ele existe, 
		 * e essa resposta for false, é só pegar o msm id e atualizar os 
		 * atributos dele no body*/
		var productModel = product0.get();
		BeanUtils.copyProperties(productDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id){
	Optional<ProductModel>product0 = productRepository.findById(id);
	if(product0.isEmpty()) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found. ");
		}
		productRepository.delete(product0.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
	}
}

