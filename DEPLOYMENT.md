# 🚀 Déploiement de l'Application - Contact Manager

## 📋 Résumé du Déploiement

**Plateforme** : Railway  
**URL de production** : https://web-production-01e1f.up.railway.app/contacts  
**Base de données** : PostgreSQL (managée par Railway)  
**Build** : Maven avec Java 21  
**Statut** : ✅ Déployée et opérationnelle

---

## 🛠 Comment l'Application a été Déployée

### 1️⃣ Préparation du Code Source

Le code source a été poussé vers le repository GitHub :

```bash
git init
git add .
git commit -m "Contact Manager - Application complète avec UI moderne"
git branch -M main
git remote add origin https://github.com/Idir0u/contact-manager.git
git push -u origin main
```

**Repository** : `Idir0u/contact-manager`

---

### 2️⃣ Configuration des Fichiers de Déploiement

Quatre fichiers de configuration ont été créés pour Railway :

#### **railway.json**
Configuration du build et déploiement :
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -jar target/contact-manager-0.0.1-SNAPSHOT.jar",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

#### **nixpacks.toml**
Spécification de la version Java :
```toml
[phases.setup]
nixPkgs = ["openjdk21"]

[phases.build]
cmds = ["mvn clean package -DskipTests"]

[start]
cmd = "java -jar target/contact-manager-0.0.1-SNAPSHOT.jar"
```

#### **Procfile**
Commande de démarrage :
```
web: java -jar target/contact-manager-0.0.1-SNAPSHOT.jar
```

#### **.railwayignore**
Fichiers exclus du build :
```
target/
.mvn/
*.log
.env
```

---

### 3️⃣ Déploiement sur Railway

**Étapes suivies** :

1. **Connexion à Railway**
   - Accès à https://railway.app
   - Authentification avec GitHub

2. **Création du Projet**
   - Clic sur "New Project"
   - Sélection "Deploy from GitHub repo"
   - Choix du repository `Idir0u/contact-manager`

3. **Ajout de PostgreSQL**
   - Dans le projet Railway, clic sur "+ New"
   - Sélection "Database" → "Add PostgreSQL"
   - Base de données créée automatiquement

4. **Variables d'Environnement Auto-injectées**
   Railway a automatiquement configuré :
   - `DATABASE_URL` : Connection string PostgreSQL
   - `PORT` : Port assigné dynamiquement
   - `POSTGRES_USER` : Username de la base
   - `POSTGRES_PASSWORD` : Mot de passe généré
   - `RAILWAY_ENVIRONMENT` : `production`

5. **Build Automatique**
   - Railway a détecté Maven et Java
   - Exécution : `mvn clean package -DskipTests`
   - Création du JAR : `contact-manager-0.0.1-SNAPSHOT.jar`

6. **Déploiement**
   - Démarrage : `java -jar target/contact-manager-0.0.1-SNAPSHOT.jar`
   - Health check réussi
   - URL générée : https://web-production-01e1f.up.railway.app

---

### 4️⃣ Configuration Automatique de la Base de Données

L'application bascule automatiquement entre H2 (local) et PostgreSQL (production) grâce à `application.yaml` :

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:h2:mem:contactdb}
    username: ${POSTGRES_USER:sa}
    password: ${POSTGRES_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: ${SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT:org.hibernate.dialect.PostgreSQLDialect}

server:
  port: ${PORT:8081}
```

**Détection automatique** :
- ✅ **Local** : Utilise H2 avec les valeurs par défaut
- ✅ **Railway** : Utilise PostgreSQL avec les variables injectées

---

### 5️⃣ Résultat du Déploiement

✅ **Application déployée** : https://web-production-01e1f.up.railway.app/contacts  
✅ **API REST accessible** : https://web-production-01e1f.up.railway.app/contacts/api  
✅ **Swagger UI** : https://web-production-01e1f.up.railway.app/swagger-ui.html  
✅ **Base de données PostgreSQL** : Connectée et opérationnelle  
✅ **HTTPS automatique** : Certificat SSL géré par Railway  
✅ **CI/CD actif** : Auto-déploiement sur `git push`

---

## 🔄 Pipeline de Déploiement Continu

```
GitHub (push) → Railway (détection) → Build Maven → Tests → Package JAR → Deploy → Live ✅
```

**Déclencheur** : Chaque `git push` sur la branche `main` déclenche un nouveau déploiement automatique.

---

## 📊 Caractéristiques Techniques du Déploiement

| Aspect | Configuration |
|--------|---------------|
| **Plateforme** | Railway |
| **Région** | Auto-sélectionnée |
| **Java Version** | OpenJDK 21 |
| **Build Tool** | Maven 3.9+ |
| **Base de données** | PostgreSQL (version managée) |
| **HTTPS** | Automatique avec certificat SSL |
| **Port** | Dynamique (assigné par Railway) |
| **Restart Policy** | ON_FAILURE (max 10 retries) |
| **Health Check** | Automatique |
| **Logs** | Disponibles dans Railway Dashboard |

---

## 📝 Fichiers de Configuration Clés

### `pom.xml` - Dépendances PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### `.env.example` - Template des variables

```env
# Local Development (H2)
DATABASE_URL=jdbc:h2:mem:contactdb
POSTGRES_USER=sa
POSTGRES_PASSWORD=
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect
PORT=8081

# Production (Railway) - Variables auto-injectées
# DATABASE_URL=postgresql://...
# POSTGRES_USER=postgres
# POSTGRES_PASSWORD=***
# PORT=***
```

---

## 🎯 Points Clés du Succès du Déploiement

1. ✅ **Configuration flexible** : Basculement automatique H2/PostgreSQL
2. ✅ **Build optimisé** : Skip des tests avec `-DskipTests` pour rapidité
3. ✅ **Variables d'environnement** : Gestion via Railway (pas de hardcoding)
4. ✅ **Fichiers de config** : `railway.json`, `nixpacks.toml`, `Procfile`
5. ✅ **Java 21** : Version moderne avec nixpacks
6. ✅ **Restart automatique** : Politique de retry en cas d'erreur
7. ✅ **CI/CD natif** : Intégration GitHub pour auto-déploiement

---

## 🔍 Vérification Post-Déploiement

Après le déploiement, les vérifications suivantes ont été effectuées :

- ✅ Page d'accueil accessible (`/contacts`)
- ✅ Création de contacts fonctionnelle
- ✅ Recherche opérationnelle
- ✅ Pagination correcte
- ✅ Mode sombre persistant
- ✅ Notifications toast visibles
- ✅ Design responsive (mobile/desktop)
- ✅ API REST accessible
- ✅ Swagger UI fonctionnel
- ✅ Base PostgreSQL connectée

---

**Date de déploiement** : Novembre 2025  
**Développeur** : SEOMANIAK  
**Statut** : ✅ Production
