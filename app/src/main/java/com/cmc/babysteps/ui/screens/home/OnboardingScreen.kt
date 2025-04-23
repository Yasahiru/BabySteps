package com.cmc.babysteps.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch


//val onboardingPages = listOf(
//    OnboardingPage(R.drawable.logo, "Bienvenue future maman", "Suivez votre grossesse semaine par semaine."),
//    OnboardingPage(R.drawable.logo, "Conseils personnalisés", "Recevez des conseils adaptés à chaque étape."),
//    OnboardingPage(R.drawable.logo, "Rappels et suivi", "N'oubliez aucun rendez-vous important.")
//)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val babyPink = Color(0xFFF8BBD0)
    val lightPurple = Color(0xFFD1C4E9)
    val softBlue = Color(0xFFBBDEFB)

    val pages = listOf(
        OnboardPage("Welcome!", "Track your pregnancy week by week", softBlue),
        OnboardPage("Follow Up", "Stay informed with weekly tips", lightPurple),
        OnboardPage("Advice", "Receive advice tailored for you", babyPink),
        OnboardPage("Stay Connected", "Join a community of supportive moms", Color(0xFFFCE4EC))
    )



    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )


    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val p = pages[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(p.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(p.description, fontSize = 16.sp)
                }
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < pages.lastIndex) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onDone()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next")
        }
    }
}

data class OnboardPage(val title: String, val description: String, val bgColor: Color)
