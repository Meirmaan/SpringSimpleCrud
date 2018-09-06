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
	Long id = (long) 1;
 	
	@Override
	protected void init(VaadinRequest request) {
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
		
		binder.bindInstanceFields(this);
		binder.setBean(person);
		save.addClickListener(e -> save());
		update.setDisableOnClick(true);
		update.addClickListener(e -> update((long) id));
		
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
	private void save() {
		person = new Person(name.getValue(), surname.getValue());
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
    	name.setValue(p.getName());
    	surname.setValue(p.getSurname());
    	id = p.getId();
    	update.setDisableOnClick(false);
    	//p.setName(name.getValue());
        //p.setSurname(surname.getValue());
        //updateGrid(filter.getValue());
        name.focus();
    }
    
    private void update(Long id) {
        perRep.getOne((long) id).setName(name.getValue());
        perRep.getOne((long) id).setSurname(surname.getValue());
        update.setDisableOnClick(true);
        updateGrid(filter.getValue());
    }

	public void updateGrid(String filterText) {
		if(filterText.isEmpty()) {
			grid.setItems(perRep.findAll());
		}
		else {
			grid.setItems(perRep.findBySurnameStartsWithIgnoreCase(filterText));
		}
		//List<Person> people = perRep.findAll();
		//grid.setItems(people);
	}
	

}