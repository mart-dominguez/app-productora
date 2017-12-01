/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavha.cap.java.aplicacion.productora;

import com.mavha.cap.java.aplicacion.productora.modelo.DetallePedido;
import com.mavha.cap.java.aplicacion.productora.modelo.Pedido;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author martdominguez
 */
@SessionScoped
@Named("pedidosController")
@JMSDestinationDefinition(
        name = "cola1",
        interfaceName = "javax.jms.Queue")
public class PedidoController implements Serializable {

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "cola1")
    private static Queue queue;

    private Pedido pedido;
    private DetallePedido detalle;

    public String nuevoPedido() {
        pedido = new Pedido();
        pedido.setFecha(new Date());
        pedido.setDetalle(new ArrayList<DetallePedido>());
        return null;
    }

    public String agregarProducto() {
        if (pedido != null && detalle != null) {
            pedido.getDetalle().add(detalle);
        }
        detalle = new DetallePedido();
        return null;
    }

    public void doEnviarMensaje() {
        try {
            System.out.println(" MENSAJE ENVIADO!!!");
            final JAXBContext jc = JAXBContext.newInstance(Pedido.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(pedido, System.out);
            StringWriter sw = new StringWriter();
            m.marshal(pedido, sw);
            //        context.createProducer().send(queue, "a:"+this.texto+";b"+this.valor);
            try (JMSContext context1 = connectionFactory.createContext();) {
                TextMessage myMsg = context1.createTextMessage();
                myMsg.setBooleanProperty("ES_PEDIDO", true);
                myMsg.setText(sw.toString());
                context1.createProducer().send(queue,myMsg );
            } catch (JMSException ex) {
                Logger.getLogger(PedidoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(PedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JMSContext getContext() {
        return context;
    }

    public void setContext(JMSContext context) {
        this.context = context;
    }

    public static ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public static void setConnectionFactory(ConnectionFactory connectionFactory) {
        PedidoController.connectionFactory = connectionFactory;
    }

    public static Queue getQueue() {
        return queue;
    }

    public static void setQueue(Queue queue) {
        PedidoController.queue = queue;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public DetallePedido getDetalle() {
        return detalle;
    }

    public void setDetalle(DetallePedido detalle) {
        this.detalle = detalle;
    }

}
