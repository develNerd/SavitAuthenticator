package org.savit.savitauthenticator.ui.genericviews

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import kotlinx.coroutines.launch
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.ui.theme.*

/*@Preview(showBackground = true)
@Composable
fun SampleRect(){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasSize = size
        val canvasWidth = size.width
        val canvasHeight = size.height

        val paint = Paint()
        paint.blendMode = BlendMode.Difference

        drawRect(
            color = Color.Gray,
            topLeft = Offset(x = canvasWidth / 4F, y = canvasHeight / 2.5F),
            size = Size(canvasWidth/2F,canvasHeight/5F),
            blendMode = paint.blendMode
        )

        drawRect(
            color = blackTrans,
            size = size
        )


    }

}*/

typealias isChecked = (ischecked:Boolean) -> Unit

data class Arc(
    val startAngle:Float,
    val color:Color
)

@Composable
fun CustomSwitch(colorWhenDisabled:Color,isChecked: Boolean,isSwitchChecked:isChecked){

    val animationScope = rememberCoroutineScope()




    val animatableX = remember { Animatable(initialValue = if (!isChecked) 3.2f else 1.44F) }


    val transition = updateTransition(targetState = animatableX, label = "trans")

    val color by transition.animateColor (label = "trans") { state ->
       if (state.targetValue == 3.2f) Green200 else colorWhenDisabled
    }

    val color2 by transition.animateColor (label = "trans") { state ->
        if (state.targetValue == 3.2f) colorWhenDisabled else Green200
    }


    val interactionSource = remember { MutableInteractionSource() }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Transparent, shape = RoundedCornerShape(3.dp))
        .clickable(interactionSource = interactionSource, indication = null) {
            animationScope.launch {
                if (!isChecked) {
                    animatableX.animateTo(
                        targetValue = 1.44F,
                        animationSpec = tween(durationMillis = 600)
                    )
                    isSwitchChecked(true)
                } else {
                    animatableX.animateTo(
                        targetValue = 3.2F,
                        animationSpec = tween(durationMillis = 600)
                    )
                    isSwitchChecked(false)
                }

            }
        }) {
        val canvasWidth = size.width
        val canvasHeight = size.height
      /*  drawCircle(
            color = Color.Gray,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension / 4
        )*/

        drawRoundRect(
            color = color2,
            topLeft = Offset(x = canvasWidth / 12F, y = canvasHeight / 4F),
            size = Size(canvasWidth/1.2F,canvasHeight/2F),
            cornerRadius = CornerRadius(canvasWidth/4f)
        )

  /*      drawCircle(
            color = Color.DarkGray,
            center = Offset(x = canvasWidth / 3.2F, y = canvasHeight / 2F),
            radius = size.minDimension / 5.5F
        )*/
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / animatableX.value, y = canvasHeight / 2F),
            radius = size.minDimension / 5.5F
        )
    }

}


@Composable
fun CustomTimeSwitch(colorWhenDisabled:Color,isChecked: Boolean,reset:Boolean,isSwitchChecked:isChecked){

    val animationScope = rememberCoroutineScope()




    val animatableX = remember { Animatable(initialValue = if (!isChecked) 3.2f else 1.44F) }


    val transition = updateTransition(targetState = animatableX, label = "trans")

    val color by transition.animateColor (label = "trans") { state ->
        if (state.targetValue == 3.2f) Green200 else colorWhenDisabled
    }

    val color2 by transition.animateColor (label = "trans") { state ->
        if (state.targetValue == 3.2f) colorWhenDisabled else Green200
    }


    val interactionSource = remember { MutableInteractionSource() }


    LaunchedEffect(key1 = reset){
        if (reset) {
            animatableX.animateTo(
                targetValue = 3.2F,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Transparent, shape = RoundedCornerShape(3.dp))
        .clickable(interactionSource = interactionSource, indication = null) {
            animationScope.launch {
                if (!isChecked) {
                    animatableX.animateTo(
                        targetValue = 1.44F,
                        animationSpec = tween(durationMillis = 600)
                    )
                    isSwitchChecked(true)
                } else {
                    animatableX.animateTo(
                        targetValue = 3.2F,
                        animationSpec = tween(durationMillis = 600)
                    )
                    isSwitchChecked(false)
                }

            }
        }) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        /*  drawCircle(
              color = Color.Gray,
              center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
              radius = size.minDimension / 4
          )*/

        drawRoundRect(
            color = color2,
            topLeft = Offset(x = canvasWidth / 12F, y = canvasHeight / 4F),
            size = Size(canvasWidth/1.2F,canvasHeight/2F),
            cornerRadius = CornerRadius(canvasWidth/4f)
        )

        /*      drawCircle(
                  color = Color.DarkGray,
                  center = Offset(x = canvasWidth / 3.2F, y = canvasHeight / 2F),
                  radius = size.minDimension / 5.5F
              )*/
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / animatableX.value, y = canvasHeight / 2F),
            radius = size.minDimension / 5.5F
        )
    }

}


@Preview(showBackground = true)
@Composable
fun PreferenceCard(){
    val isDark = isSystemInDarkTheme()
    val color = if (isDark) radioBg else radioBg
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .height(70.dp)
        .background(color = if (isDark) GreenTrans200 else Green201, shape = RoundedCornerShape(10))) {
        
        Box(modifier = Modifier
            .size(60.dp)
            .align(Alignment.CenterEnd)) {
            CustomSwitch(colorWhenDisabled = color,true){ischecked ->

            }
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun CustomProgressBarPreview(){
    val stroke2 = with(LocalDensity.current) { Stroke(1.dp.toPx()) }
    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
    var progressMutiplyingFactor by remember {
        mutableStateOf(1F)
    }

    val progressBarProgess = derivedStateOf {
        360F * progressMutiplyingFactor
    }


    val progress by animateFloatAsState(targetValue = progressBarProgess.value,animationSpec = tween(600))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .clickable {
                progressMutiplyingFactor -= 0.05F
            }
            .padding(5.dp))
        {

            val innerRadius = (size.minDimension - stroke2.width) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )

            val size1 = Size(innerRadius * 2, innerRadius * 2)

            if (progressBarProgess.value <= 360F && progressBarProgess.value > 0){
                drawArc(
                    color = red,
                    startAngle = -90F + 1.8F/2,
                    sweepAngle = if (progressBarProgess.value < 120F) progress - 1.8f  else 120F - 1.8F ,
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )

                drawArc(
                    color = lightblue,
                    startAngle = 30F + (1.8f / 2),
                    sweepAngle = if (progressBarProgess.value <= 120F)  0F else if (progressBarProgess.value > 120F && progressBarProgess.value < 240F) progress - (120F - 1.8f) else (120F - 1.8f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )


                drawArc(
                    color = Color.Green,
                    startAngle = 150F + (1.8f / 2) ,
                    sweepAngle = if (progressBarProgess.value <= 240F)  0F else progress - (240F),
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )
            } else  {
                progressMutiplyingFactor = 1F
            }



            /*drawArc(
                color = Color.Blue,
                startAngle = 30F + (1.8f * 1.5f),
                sweepAngle = 120F - (1.8f * 1.5f),
                useCenter = false,
                topLeft = topLeft,
                size = size1,
                style = stroke
            )
    
            drawArc(
                color = Color.Green,
                startAngle = 150F + (1.8f * 1.5f),
                sweepAngle = 120F - (1.8f * 1.5f),
                useCenter = false,
                topLeft = topLeft,
                size = size1,
                style = stroke
            )*/
        }
        Text(text = "12",modifier = Modifier.align(Alignment.Center),fontSize = 14.sp)


    }
    
  
    
    
}

@Composable
fun CustomProgressBar(progressMutiplyingFactor:Float,secondsRemaining:String){
    val stroke2 = with(LocalDensity.current) { Stroke(1.dp.toPx()) }
    val stroke = with(LocalDensity.current) { Stroke(3.dp.toPx()) }


    val progressBarProgess = derivedStateOf {
        360F * progressMutiplyingFactor
    }


    val progress by animateFloatAsState(targetValue = progressBarProgess.value,animationSpec = tween(200))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp))
        {

            val innerRadius = (size.minDimension - stroke2.width) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )

            val size1 = Size(innerRadius * 2, innerRadius * 2)

            if (progressBarProgess.value <= 360F && progressBarProgess.value > 0){
                drawArc(
                    color = red,
                    startAngle = -90F + 1.8F/2,
                    sweepAngle = if (progressBarProgess.value < 120F) progress - 1.8f  else 120F - 1.8F ,
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )

                drawArc(
                    color = lightblue,
                    startAngle = 30F + (1.8f / 2),
                    sweepAngle = if (progressBarProgess.value <= 120F)  0F else if (progressBarProgess.value > 120F && progressBarProgess.value < 240F) progress - (120F - 1.8f) else (120F - 1.8f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )


                drawArc(
                    color = Color.Green,
                    startAngle = 150F + (1.8f / 2) ,
                    sweepAngle = if (progressBarProgess.value <= 240F)  0F else progress - (240F),
                    useCenter = false,
                    topLeft = topLeft,
                    size = size1,
                    style = stroke
                )
            } else  {

            }



            /*drawArc(
                color = Color.Blue,
                startAngle = 30F + (1.8f * 1.5f),
                sweepAngle = 120F - (1.8f * 1.5f),
                useCenter = false,
                topLeft = topLeft,
                size = size1,
                style = stroke
            )

            drawArc(
                color = Color.Green,
                startAngle = 150F + (1.8f * 1.5f),
                sweepAngle = 120F - (1.8f * 1.5f),
                useCenter = false,
                topLeft = topLeft,
                size = size1,
                style = stroke
            )*/
        }
        Text(text = secondsRemaining,modifier = Modifier.align(Alignment.Center),fontSize = 14.sp)


    }




}

@Composable
fun Loader() {
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
        .padding(3.dp)) {
        val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.loader) }
        // You can control isPlaying/progress/repeat/etc. with this.
        val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Integer.MAX_VALUE)
        animationState.speed = 2F
        LottieAnimation(spec = animationSpec,animationState = animationState,modifier = Modifier
            .width(64.dp)
            .align(
                Alignment.Center
            ))

    }

}


@Composable
fun InputField(inputFieldValue:String,inputFieldHint:String,onInputValueChange:(String) -> Unit){

    Column(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            maxLines = 1,
            singleLine = true,
            value = inputFieldValue,
            onValueChange = onInputValueChange,
            label = { Text(text = inputFieldHint) },modifier = Modifier.fillMaxWidth(),

            )
        Spacer(modifier = Modifier.height(5.dp))

    }
}