# Documentación PoC - apkQR

## Descripción General

Esta aplicación Android es una Prueba de Concepto (PoC) para el flujo de pagos mediante QR. Permite escanear un código QR, mostrar los datos del pago, simular el pago y mostrar un comprobante. El proceso está diseñado para ser simple y cumplir con los requerimientos mínimos de integración y validación.

---

## Flujo de la Aplicación

1. **Escaneo del QR**
   - El usuario escanea un código QR que contiene un JSON con los datos del pago.
   - El JSON debe incluir los siguientes campos obligatorios:
     - `client_id`
     - `nit_compania`
     - `referencia_pago`
     - `monto`
     - `iva`
     - `base`
   - Otros campos pueden estar presentes para el comprobante, pero no son validados en esta etapa.

2. **Visualización de Datos**
   - Tras el escaneo, la app muestra en pantalla los datos principales del pago:
     - Comercio (client_id y NIT)
     - Referencia de pago
     - Monto
     - IVA
     - Base
   - El estado inicial es "Vigente" y el botón "Pagar" está habilitado.

3. **Temporizador de Vigencia**
   - Al escanear el QR, se inicia un temporizador local de **30 segundos**.
   - Durante este tiempo, el usuario puede revisar los datos y presionar "Pagar".
   - No se realiza ninguna validación de fechas ni zonas horarias; la vigencia depende únicamente del temporizador local.

4. **Vencimiento del QR**
   - Si pasan 30 segundos sin que el usuario pague, la app muestra automáticamente la pantalla de "QR vencido" (VencimientoQRActivity).
   - El usuario ya no puede realizar el pago y debe volver a escanear un nuevo QR.

5. **Simulación de Pago**
   - Si el usuario presiona "Pagar" antes de que venza el QR, se simula el pago y se muestra un comprobante con los datos relevantes.

---

## Consideraciones Técnicas

- **No se valida la hora del dispositivo ni se compara con ninguna fecha del QR.**
- **No se usa el campo `fecha_expiracion` ni `tiempo_validez_segundos`.**
- El backend solo debe enviar el campo `fecha_expedicion` (valor fijo) y los datos del pago.
- El temporizador de 30 segundos es local y comienza al escanear el QR.
- Si el QR no contiene los campos obligatorios, se muestra un mensaje de error y no se permite el pago.

---

## Requerimientos para el Backend

- El QR debe contener un JSON con los campos obligatorios mencionados.
- El campo `fecha_expedicion` es solo informativo y no se usa para lógica de vigencia.
- No es necesario enviar campos de expiración ni tiempo de validez.

---

## Resumen del Proceso

1. Escanear QR → Mostrar datos → Iniciar temporizador de 30s
2. Si el usuario paga antes de 30s → Mostrar comprobante
3. Si pasan 30s sin pagar → Mostrar pantalla de QR vencido

---

## Notas para Pruebas

- El flujo es completamente local y no depende de la hora del dispositivo ni de la generación del QR.
- Para reiniciar el proceso, basta con volver a escanear un QR válido.
- El temporizador se reinicia con cada nuevo escaneo.

---

## Contacto

Para dudas o soporte técnico, contactar al equipo de desarrollo
