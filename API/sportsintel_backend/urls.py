from django.urls import path, include

urlpatterns = [
    path('api/players/', include('players.urls')),
]
