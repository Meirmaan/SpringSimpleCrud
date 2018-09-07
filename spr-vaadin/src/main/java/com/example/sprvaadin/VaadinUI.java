package com.example.sprvaadin;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SpringUI
@Theme("valo")
public class VaadinUI extends UI{

	//@Autowired
	//private Service editor;
	@Autowired
	private PersonRepository perRep;
	//@Autowired
	private Person person;
	Grid<Person> grid;
	TextField filter;
	
	//from Srvice class
	TextField name = new TextField();
	TextField surname = new TextField();
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button update = new Button("Update");
	Binder<Person> binder = new Binder<>(Person.class);
	//Long id = (long) 1;
 	
	@Override
	protected void init(VaadinRequest request) {
		
		binder.bindInstanceFields(this);
		//editor = new Service(perRep);
		grid = new Grid<>(Person.class);
		filter = new TextField();
		
		filter.setPlaceholder("Filter by surname");
        //filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> updateGrid(e.getValue()));
				
		HorizontalLayout head = new HorizontalLayout();
		head.addComponents(filter);
		
		//from Service class
		name.setPlaceholder("Enter name");
		surname.setPlaceholder("Enter surname");
		
		HorizontalLayout actions = new HorizontalLayout(save, update);
		VerticalLayout editor = new VerticalLayout(name, surname, actions);
		
		//binder.setBean(person);
		save.addClickListener(e -> save());
		//update.setDisableOnClick(true);
		update.addClickListener(e -> update(person));
		
		HorizontalLayout main = new HorizontalLayout();
		main.addComponents(grid, editor);
		
		//grid.setHeight("300px");
		grid.setColumns("id", "name", "surname");
        grid.addComponentColumn(this::buildDeleteButton);
        grid.addComponentColumn(this::buildEditButton);
		updateGrid(filter.getValue());
		 		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponents(head, main);  
		
		
		setContent(mainLayout);
	
	}
	
	//from Service class 
	public void setPerson(Person person) {
	    this.person = person;
	    binder.setBean(person);
	    //BeanFieldGroup.bindFieldsUnbuffered(person, this);
	}

	private void save() {
		person = new Person(name.getValue(), surname.getValue());
		//this.person = p;
		perRep.save(person);
		updateGrid(filter.getValue());
		name.setValue("");
		surname.setValue("");
		name.focus();
	}
	
	
	private Button buildDeleteButton(Person p) {
        Button button = new Button(VaadinIcons.TRASH);
        button.addClickListener(e -> deletePerson(p));
        return button;
    }

    private void deletePerson(Person p) {
        perRep.delete(p);
        updateGrid(filter.getValue());
        filter.focus();
    }
    
    private Button buildEditButton(Person p) {
        Button button = new Button("edit");
        button.addClickListener(e -> editPerson(p));
        return button;
    }
    
    private void editPerson(Person p) {
    	this.person = p;
    	name.setValue(person.getName());
    	surname.setValue(person.getSurname());
	    binder.setBean(person);
        name.focus();
    }
    
    private void update(Person p) {
    	perRep.save(person);
        updateGrid(filter.getValue());
    }
    

	public void updateGrid(String filterText) {
		if(filterText.isEmpty()) {
			grid.setItems(perRep.findAll());
		}
		else {
			grid.setItems(perRep.findBySurnameStartsWithIgnoreCase(filterText));
		}
	}
	

}
