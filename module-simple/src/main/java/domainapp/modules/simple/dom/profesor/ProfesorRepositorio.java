/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.modules.simple.dom.profesor;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.datanucleus.query.typesafe.TypesafeQuery;

import java.util.List;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "simple.ProfesorMenu",
        repositoryFor = Profesor.class
)
@DomainServiceLayout(
        named = "Profesores",
        menuOrder = "10"
)
public class ProfesorRepositorio {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<Profesor> listAll() {
        return repositoryService.allInstances(Profesor.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "2")
    public List<Profesor> findByName(
            @ParameterLayout(named="Nombre") final String ingreso
    ) {
        TypesafeQuery<Profesor> q = isisJdoSupport.newTypesafeQuery(Profesor.class);
        final QProfesor cand = QProfesor.candidate();
        q = q.filter(
                cand.nombre.indexOf(q.stringParameter("ingreso")).ne(-1).or(
                cand.apellido.indexOf(q.stringParameter("ingreso")).ne(-1)
                )
        );
        return q.setParameter("ingreso", ingreso)
                .executeList();
    }

    @Programmatic
    public Profesor findByNameExact(final String nombre) {
        TypesafeQuery<Profesor> q = isisJdoSupport.newTypesafeQuery(Profesor.class);
        final QProfesor cand = QProfesor.candidate();
        q = q.filter(
                cand.nombre.eq(q.stringParameter("nombre"))
        );
        return q.setParameter("nombre", nombre)
                .executeUnique();
    }

    @Programmatic
    public void ping() {
        TypesafeQuery<Profesor> q = isisJdoSupport.newTypesafeQuery(Profesor.class);
        final QProfesor candidate = QProfesor.candidate();
        q.range(0,2);
        q.orderBy(candidate.nombre.asc());
        q.executeList();
    }

    public static class CreateDomainEvent extends ActionDomainEvent<ProfesorRepositorio> {}

    @Action(domainEvent = CreateDomainEvent.class)
    @MemberOrder(sequence = "3")
    public Profesor create(
            @ParameterLayout(named="Nombre") final String nombre,
            @ParameterLayout(named="Apellido") final String apellido,
            @ParameterLayout(named="Documento") final String documento) {
        return repositoryService.persist(new Profesor(nombre, apellido, documento));
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}
