package ar.com.syswarp.views.cargahoras;

import ar.com.syswarp.data.entity.Horas;
import ar.com.syswarp.data.service.HorasService;
import ar.com.syswarp.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Carga Horas")
@Route(value = "carga/:horasID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class CargaHorasView extends Div implements BeforeEnterObserver {

    private final String HORAS_ID = "horasID";
    private final String HORAS_EDIT_ROUTE_TEMPLATE = "carga/%s/edit";

    private Grid<Horas> grid = new Grid<>(Horas.class, false);

    private DatePicker fecha;
    private TextField cantidadhoras;
    private TextField observaciones;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Horas> binder;

    private Horas horas;

    private final HorasService horasService;

    @Autowired
    public CargaHorasView(HorasService horasService) {
        this.horasService = horasService;
        addClassNames("carga-horas-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("fecha").setAutoWidth(true);
        grid.addColumn("cantidadhoras").setAutoWidth(true);
        grid.addColumn("observaciones").setAutoWidth(true);
        grid.setItems(query -> horasService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(HORAS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CargaHorasView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Horas.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(cantidadhoras).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("cantidadhoras");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.horas == null) {
                    this.horas = new Horas();
                }
                binder.writeBean(this.horas);
                horasService.update(this.horas);
                clearForm();
                refreshGrid();
                Notification.show("Horas details stored.");
                UI.getCurrent().navigate(CargaHorasView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the horas details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> horasId = event.getRouteParameters().get(HORAS_ID).map(UUID::fromString);
        if (horasId.isPresent()) {
            Optional<Horas> horasFromBackend = horasService.get(horasId.get());
            if (horasFromBackend.isPresent()) {
                populateForm(horasFromBackend.get());
            } else {
                Notification.show(String.format("The requested horas was not found, ID = %s", horasId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CargaHorasView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        fecha = new DatePicker("Fecha");
        cantidadhoras = new TextField("Cantidadhoras");
        observaciones = new TextField("Observaciones");
        Component[] fields = new Component[]{fecha, cantidadhoras, observaciones};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Horas value) {
        this.horas = value;
        binder.readBean(this.horas);

    }
}
