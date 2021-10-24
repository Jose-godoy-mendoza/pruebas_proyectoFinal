/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author joseg
 */
public class Compras_detalle {
    private int idcompra_detalle,idcompra,idproducto,cantidad;
    private double precio_costo_unitario;
    Conexion cn;
    
    public Compras_detalle(){}
    
    public Compras_detalle(int idcompra_detalle, int idcompra, int idproducto, int cantidad, double precio_costo_unitario) {
        this.idcompra_detalle = idcompra_detalle;
        this.idcompra = idcompra;
        this.idproducto = idproducto;
        this.cantidad = cantidad;
        this.precio_costo_unitario = precio_costo_unitario;
    }
    
    
    
    public int getIdcompra_detalle() {
        return idcompra_detalle;
    }

    public void setIdcompra_detalle(int idcompra_detalle) {
        this.idcompra_detalle = idcompra_detalle;
    }

    public int getIdcompra() {
        return idcompra;
    }

    public void setIdcompra(int idcompra) {
        this.idcompra = idcompra;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_costo_unitario() {
        return precio_costo_unitario;
    }

    public void setPrecio_costo_unitario(double precio_costo_unitario) {
        this.precio_costo_unitario = precio_costo_unitario;
    }
    
    public int idcompra_max()
    {
            int mayor_idc=0;
            try
            {
            cn=new Conexion();
            cn.abrir_conexion();
            String buscar_idc_mayor="select MAX(idcompra) from compras";
            ResultSet consulta=cn.conexionBD.createStatement().executeQuery(buscar_idc_mayor);
            while(consulta.next())
            {
                mayor_idc=consulta.getInt(1);
            }
            cn.cerrar_conexion();
            }catch(Exception ex)
            {
                
            }
            return mayor_idc;
    }
    
     public int modificar()
    {
        int devolver=0;
        try
        {
            cn = new Conexion();
            cn.abrir_conexion();
            PreparedStatement parametros;
            String consulta_sql="update compras_detalle set  idproducto=?, cantidad=?, precio_costo_unitario=? where idcompra_detalle=?";
            parametros=(PreparedStatement)cn.conexionBD.prepareStatement(consulta_sql);
            
            parametros.setInt(1, getIdproducto());
            parametros.setInt(2, getCantidad());
            parametros.setDouble(3, getPrecio_costo_unitario());
            parametros.setInt(4, getIdcompra_detalle());
            
            devolver=parametros.executeUpdate();
            
            cn.cerrar_conexion();
        }catch(SQLException ex)
        {
            
        }
        
        return devolver;
    }
    
    public int agregar()
    {
        int retorno=0;
        //double precio_u=precio_unitario(getIdproducto());
        try
        {
            PreparedStatement parametro;
            String  insertar = "insert into compras_detalle(idcompra,idproducto,cantidad,precio_costo_unitario) VALUES (?,?,?,?)";
            cn=new Conexion();
            cn.abrir_conexion();
            parametro=(PreparedStatement) cn.conexionBD.prepareStatement(insertar);
            parametro.setInt(1, getIdcompra());
            parametro.setInt(2, getIdproducto());
            parametro.setInt(3, getCantidad());
            parametro.setDouble(4, getPrecio_costo_unitario());
            retorno=parametro.executeUpdate();
            modificar_costo_prod();
            cn.cerrar_conexion();
        }catch(Exception ex)
        {

        }
        return retorno;
    }
    
    public int modificar_costo_prod()
    {
        double precio_costo=getPrecio_costo_unitario();
        double precio_venta=precio_costo*1.25;
        int devolver=0;
        try
        {
            PreparedStatement parametro;
            String codigo_sql="update db_punto_venta.productos set precio_costo=?, precio_venta=?, existencia=existencia+? where idproducto=?";
            cn = new Conexion();
            cn.abrir_conexion();
            parametro=(PreparedStatement) cn.conexionBD.prepareStatement(codigo_sql);
            
            parametro.setDouble(1, getPrecio_costo_unitario());
            parametro.setDouble(2, precio_venta);
            parametro.setDouble(3, getCantidad());
            parametro.setInt(4, getIdproducto());
            
            devolver=parametro.executeUpdate();
            cn.cerrar_conexion();
        }catch(HeadlessException | SQLException ex)
        {
            System.out.println("error........"+ex.getMessage());
        }
        
        return devolver;
    }
    
    public DefaultTableModel leer()
    {
        DefaultTableModel tabla = new DefaultTableModel();
        try
        {
            cn = new Conexion();
            String query="select cd.idcompra_detalle as id, c.no_orden_compra, p.proveedor, prod.producto, cd.cantidad, cd.precio_costo_unitario, c.fecha_orden, c.fechaingreso, c.idproveedor, cd.idproducto, cd.idcompra from compras_detalle as cd INNER JOIN compras c on cd.idcompra=c.idcompra INNER JOIN proveedores p on c.idproveedor=p.idproveedor INNER JOIN productos prod on cd.idproducto=prod.idproducto;";
            cn.abrir_conexion();
            ResultSet consulta=cn.conexionBD.createStatement().executeQuery(query);
            String encabezado[]={"id","no_orden","proveedor","producto","cantidad","precio_unitario","fecha_orden","fecha_ingreso","idproveedor","idproducto","idcompra"};
            tabla.setColumnIdentifiers(encabezado);
            String datos[]= new String [12];
            while(consulta.next())
            {
                datos[0] = consulta.getString("id");
                datos[1] = consulta.getString("no_orden_compra");
                datos[2] = consulta.getString("proveedor");
                datos[3] = consulta.getString("producto");
                datos[4] = consulta.getString("cantidad");

                datos[5] = consulta.getString("precio_costo_unitario");
                datos[6] = consulta.getString("fecha_orden");
                datos[7] = consulta.getString("fechaingreso");
                datos[8] = consulta.getString("idproveedor");
                datos[9] = consulta.getString("idproducto");
                datos[10]=consulta.getString("idcompra");
                tabla.addRow(datos);
            }
            cn.cerrar_conexion();
        }catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getMessage());
        }
        return tabla;
    }
}
