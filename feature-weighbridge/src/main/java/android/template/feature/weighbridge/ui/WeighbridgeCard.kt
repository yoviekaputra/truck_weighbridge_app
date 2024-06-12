package android.template.feature.weighbridge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeighbridgeCard(
    data: WeighbridgeUiModel,
    onEditClick: (WeighbridgeUiModel) -> Unit,
    onDeleteClick: (WeighbridgeUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.outlinedCardElevation(),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column {
                    Text(text = data.licenceNumber, fontWeight = FontWeight.Bold)
                    Text(text = data.driverName, fontSize = 14.sp)
                }

                Text(
                    text = data.datetimeText,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Net Weight", fontSize = 12.sp)

                Text(
                    text = data.netWeight, fontWeight = FontWeight.Bold, fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("In", fontSize = 14.sp, modifier = Modifier.weight(0.15f))
                        Text(
                            text = data.inboundWeightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Out",
                            fontSize = 14.sp,
                            modifier = Modifier.weight(0.15f)
                        )
                        Text(
                            text = data.outboundWeightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                ) {
                    CardActionButton(
                        image = Icons.Filled.Delete,
                        tint = Color.Red,
                        contentDescription = "click delete ${data.id} "
                    ) { onDeleteClick(data) }

                    Spacer(modifier = Modifier.width(8.dp))

                    CardActionButton(
                        image = Icons.Filled.Edit,
                        tint = Color.Black,
                        contentDescription = "click edit ${data.id} "
                    ) { onEditClick(data) }
                }
            }
        }
    }
}

@Composable
private fun CardActionButton(
    modifier: Modifier = Modifier,
    image: ImageVector,
    tint: Color,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .requiredSize(28.dp),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = image,
            tint = tint,
            contentDescription = contentDescription
        )
    }
}