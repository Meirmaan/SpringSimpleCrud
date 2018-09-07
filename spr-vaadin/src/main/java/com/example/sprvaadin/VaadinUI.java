package com.example.sprvaadin;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SpringUI
@Theme("valo")
public class VaadinUI extends UI{

	@Autowired
	private PersonRepository perRep;
	private Person person;
	Grid<Person> grid;
	TextField filter;
	
	TextField name = new TextField();
	TextField surname = new TextField();
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button update = new Button("Update");
	Binder<Person> binder = new Binder<>(Person.class);
 	
	@Override
	protected void init(VaadinRequest request) {
		
		binder.bindInstanceFields(this);
		grid = new Grid<>(Person.class);
		filter = new TextField();
		
		filter.setPlaceholder("Filter by surname");
		filter.addValueChangeListener(e -> updateGrid(e.getValue()));
				
		HorizontalLayout head = new HorizontalLayout();
		head.addComponents(filter);
		
		name.setPlaceholder("Enter name");
		surname.setPlaceholder("Enter surname");
		
		HorizontalLayout actions = new HorizontalLayout(save, update);
		VerticalLayout editor = new VerticalLayout(name, surname, actions);
		
		save.addClickListener(e -> save());
		update.setVisible(false);
		update.addClickListener(e -> update(person));
		
		HorizontalLayout main = new HorizontalLayout();
		main.addComponents(grid, editor);
		
		grid.setColumns("id", "name", "surname");
        grid.addComponentColumn(this::buildDeleteButton);
        grid.addComponentColumn(this::buildEditButton);
		updateGrid(filter.getValue());
		 		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponents(head, main);  
		
		
		setContent(mainLayout);
	
	}

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
    	this.person = p;
    	update.setVisible(true);
    	name.setValue(person.getName());
    	surname.setValue(person.getSurname());
	    binder.setBean(person);
        name.focus();
    }
    
    private void update(Person p) {
    	perRep.save(person);
        updateGrid(filter.getValue());
        update.setVisible(false);
        name.setValue("");
        surname.setValue("");
        name.focus();
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
