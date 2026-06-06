package com.example.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Delivery
import java.text.SimpleDateFormat
import java.util.*

// Shopee Cores
val ShopeeOrange = Color(0xFFEE4D2D)
val ShopeeDarkOrange = Color(0xFFD73E1B)
val ShopeeLightBg = Color(0xFFF6F6F6)
val ShopeeGreen = Color(0xFF26AA99)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DeliveryScreen(
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val deliveries by viewModel.deliveriesState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Estados do Formulário
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    
    // Obter data atual formatada
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDateStr = dateFormat.format(Date())
    var deliveryDate by remember { mutableStateOf(currentDateStr) }

    // Estados de Validação
    var customerNameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    // Configuração do DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedMonth = String.format(Locale.getDefault(), "%02d", month + 1)
            val formattedDay = String.format(Locale.getDefault(), "%02d", dayOfMonth)
            deliveryDate = "$year-$formattedMonth-$formattedDay"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Formatar data para exibição mais bonita para o usuário brasileiro (dd/MM/yyyy)
    val displayDate = remember(deliveryDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            parser.parse(deliveryDate)?.let { formatter.format(it) } ?: deliveryDate
        } catch (e: Exception) {
            deliveryDate
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ShopeeLightBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header correspondente ao topo do mockup HTML
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 480.dp)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShopeeOrange)
                            .testTag("app_logo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📦", // Caracter simulando ícone de caixa do HTML mockup
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Shopee Entregas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }

            // Estatísticas rápidas de itens registrados
            if (deliveries.isNotEmpty()) {
                item {
                    val totalPacotes = deliveries.sumOf { it.quantity }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 480.dp),
                        colors = CardDefaults.cardColors(containerColor = ShopeeGreen.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total de Encomendas",
                                    fontSize = 13.sp,
                                    color = Color(0xFF555555),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${deliveries.size} clientes atendidos",
                                    fontSize = 15.sp,
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ShopeeGreen)
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$totalPacotes itens",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bloco do Formulário
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 480.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Nome do Cliente
                        Column {
                            Text(
                                text = "Nome do Cliente",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = {
                                    customerName = it
                                    customerNameError = false
                                },
                                placeholder = { Text("Ex: João Silva") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = customerNameError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShopeeOrange,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    errorBorderColor = MaterialTheme.colorScheme.error,
                                    focusedPlaceholderColor = Color(0xFF9E9E9E)
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Ícone de Pessoa",
                                        tint = if (customerNameError) MaterialTheme.colorScheme.error else Color(0xFF9E9E9E)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            if (customerNameError) {
                                Text(
                                    text = "O nome do cliente é obrigatório",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Celular com auto formatação brasileira ((XX) XXXXX-XXXX)
                        Column {
                            Text(
                                text = "Celular",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { newVal ->
                                    // Manter somente dígitos e aplicar formatação
                                    val clean = newVal.filter { it.isDigit() }
                                    phone = if (clean.length <= 11) {
                                        formatBrazilianPhone(clean)
                                    } else {
                                        phone
                                    }
                                    phoneError = false
                                },
                                placeholder = { Text("(00) 00000-0000") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = phoneError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShopeeOrange,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Ícone de Telefone",
                                        tint = if (phoneError) MaterialTheme.colorScheme.error else Color(0xFF9E9E9E)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                )
                            )
                            if (phoneError) {
                                Text(
                                    text = "O celular do cliente é obrigatório",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // PIN da Encomenda
                        Column {
                            Text(
                                text = "PIN da Encomenda",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = pin,
                                onValueChange = {
                                    pin = it
                                    pinError = false
                                },
                                placeholder = { Text("Código PIN") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pin_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = pinError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShopeeOrange,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Ícone Informações PIN",
                                        tint = if (pinError) MaterialTheme.colorScheme.error else Color(0xFF9E9E9E)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            if (pinError) {
                                Text(
                                    text = "O código PIN é obrigatório",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Quantidade com controles interativos +/- mais bonitos
                        Column {
                            Text(
                                text = "Quantidade de Itens",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quantity_input"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botão de menos
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear, // Usamos Clear como sinal de remover/subtrair ou desenharemos um travessão
                                        contentDescription = "Diminuir",
                                        tint = Color(0xFF555555)
                                    )
                                }
                                
                                // Quantidade centro
                                Text(
                                    text = quantity.toString(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF333333)
                                )

                                // Botão de mais
                                IconButton(
                                    onClick = { quantity++ },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Aumentar",
                                        tint = Color(0xFF555555)
                                    )
                                }
                            }
                        }

                        // Data de Retirada interativa
                        Column {
                            Text(
                                text = "Data de Retirada",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedCard(
                                onClick = { datePickerDialog.show() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("date_picker_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = displayDate,
                                        fontSize = 15.sp,
                                        color = Color(0xFF333333)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Selecionar Data",
                                        tint = ShopeeOrange
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Botão Registrar Entrega (M3 Orange)
                        Button(
                            onClick = {
                                customerNameError = customerName.isBlank()
                                phoneError = phone.isBlank()
                                pinError = pin.isBlank()

                                if (!customerNameError && !phoneError && !pinError) {
                                    viewModel.addDelivery(
                                        customerName = customerName.trim(),
                                        phone = phone.trim(),
                                        pin = pin.trim(),
                                        quantity = quantity,
                                        deliveryDate = deliveryDate
                                    )
                                    // Limpar formulário
                                    customerName = ""
                                    phone = ""
                                    pin = ""
                                    quantity = 1
                                    deliveryDate = currentDateStr
                                    focusManager.clearFocus()

                                    Toast.makeText(context, "Entrega registrada com sucesso!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("register_delivery_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ShopeeOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Registrar Entrega",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bloco de Histórico de Entregas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 480.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Histórico de Entregas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    // Campo de Pesquisa correspondente ao mockup
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Buscar por cliente ou PIN...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShopeeOrange,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Lupa de Busca",
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpar busca",
                                        tint = Color(0xFF555555)
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Exibir Entregas Registradas ou Estado Vazio
            if (deliveries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 480.dp)
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📦",
                                fontSize = 32.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Nenhuma entrega registrada",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) 
                                "Experimente buscar por outro nome ou PIN" 
                            else 
                                "Insira os dados acima para registrar sua primeira coleta.",
                            fontSize = 13.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                items(deliveries, key = { it.id }) { delivery ->
                    DeliveryItemCard(
                        delivery = delivery,
                        onDelete = { viewModel.deleteDelivery(delivery) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryItemCard(
    delivery: Delivery,
    onDelete: () -> Unit
) {
    // Formatar data local de yyyy-MM-dd para dd/MM/yyyy
    val displayFormattedDate = remember(delivery.deliveryDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            parser.parse(delivery.deliveryDate)?.let { formatter.format(it) } ?: delivery.deliveryDate
        } catch (e: Exception) {
            delivery.deliveryDate
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .testTag("delivery_card_${delivery.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo com iniciais ou quantidade
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ShopeeOrange.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${delivery.quantity}x",
                    color = ShopeeOrange,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações da entrega
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = delivery.customerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PIN: ${delivery.pin}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = delivery.phone,
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Data de Retirada",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Data: $displayFormattedDate",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            // Botão Excluir
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("delete_delivery_button_${delivery.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir Registro",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

// Formatador helper para celular celular brasileiro: (99) 99999-9999 ou (99) 9999-9999
private fun formatBrazilianPhone(cleanDigits: String): String {
    return when {
        cleanDigits.isEmpty() -> ""
        cleanDigits.length <= 2 -> "($cleanDigits"
        cleanDigits.length <= 6 -> "(${cleanDigits.substring(0, 2)}) ${cleanDigits.substring(2)}"
        cleanDigits.length <= 10 -> "(${cleanDigits.substring(0, 2)}) ${cleanDigits.substring(2, 6)}-${cleanDigits.substring(6)}"
        else -> "(${cleanDigits.substring(0, 2)}) ${cleanDigits.substring(2, 7)}-${cleanDigits.substring(7)}"
    }
}
