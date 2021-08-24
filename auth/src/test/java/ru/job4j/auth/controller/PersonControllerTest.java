package ru.job4j.auth.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository repository;

    @Test
    public void whenGetAllPersonsThenOkAndReturnAllPersons() throws Exception {
        mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(repository).findAll();
    }

    @Test
    public void whenGetPersonByIdThenOkAndReturnPerson() throws Exception {
        when(repository.findById(any())).thenReturn(Optional.of(new Person()));
        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);
        verify(repository).findById(arg.capture());
        assertThat(arg.getValue(), is(1));
    }

    @Test
    public void whenPostNewPersonThenStatusCreatedAndSavePerson() throws Exception {
        when(repository.save(any())).thenReturn(new Person());
        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test\",\"password\":\"123\"}"))
                .andDo(print())
                .andExpect(status().isCreated());
        ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(arg.capture());
        assertThat(arg.getValue().getId(), is(0));
        assertThat(arg.getValue().getLogin(), is("test"));
        assertThat(arg.getValue().getPassword(), is("123"));
    }

    @Test
    public void whenPutPersonThenStatusOkAndUpdatePerson() throws Exception {
        when(repository.save(any())).thenReturn(new Person());
        mockMvc.perform(put("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\",\"login\":\"test\",\"password\":\"123\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(arg.capture());
        assertThat(arg.getValue().getId(), is(1));
        assertThat(arg.getValue().getLogin(), is("test"));
        assertThat(arg.getValue().getPassword(), is("123"));
    }

    @Test
    public void whenDELETEPersonThenStatusOkAndDeletePerson() throws Exception {
        mockMvc.perform(delete("/person/100"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
        verify(repository).delete(arg.capture());
        assertThat(arg.getValue().getId(), is(100));
    }
}