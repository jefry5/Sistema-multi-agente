# Sistemas Multiagente
La aplicación simula un proceso de compra y despacho de productos.

## Compilación de la aplicación
```bash
javac -d out -cp ".;.\jade.jar" agents\*.java
```

## Ejecución de la aplicación
```bash
java -cp ".;jade.jar;out" jade.Boot -gui -agents "almacen:AgenteAlmacen;logistica:AgenteLogistica;vendedor:AgenteVendedor;comprador:AgenteComprador"
```