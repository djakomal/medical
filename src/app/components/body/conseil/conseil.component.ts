// medico-articles.component.ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

interface Article {
  id: string;
  title: string;
  author: string;
  date: string;
  tags: string[];
  content: string;
  headerImage: string;
  shortDescription?: string;
}

@Component({
  selector: 'app-conseil',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './conseil.component.html',
  styleUrl: './conseil.component.css',
})
export class ConseilComponent {
  isPopupOpen = false;
  selectedArticle: Article | null = null;

  private articles: Record<string, Article> = {
    dengue: {
      id: 'dengue',
      title: 'DENGUE - Comprendre et Prévenir',
      author: 'Dr. Kokou Géoffroy DISSE',
      date: '17 Jui',
      tags: ['VIRUS ET ÉPIDÉMIES'],
      content: `
        <div class="popup-section">
          <h3>Qu'est-ce que la dengue ?</h3>
          <p class="popup-text">La dengue est une maladie virale transmise par les moustiques du genre Aedes, principalement Aedes aegypti. Elle est endémique dans plus de 100 pays, particulièrement dans les régions tropicales et subtropicales.</p>
        </div>
        
        <div class="popup-section">
          <h3>Symptômes</h3>
          <p class="popup-text">Les symptômes incluent une fièvre élevée, des maux de tête intenses, des douleurs musculaires et articulaires, des nausées et parfois une éruption cutanée. Dans les cas graves, la dengue peut provoquer des complications potentiellement mortelles.</p>
        </div>
        
        <div class="popup-section">
          <h3>Prévention</h3>
          <p class="popup-text">La prévention repose principalement sur le contrôle des moustiques vecteurs : élimination des eaux stagnantes, utilisation de répulsifs, port de vêtements longs, et installation de moustiquaires.</p>
        </div>
      `,
      headerImage: `
         <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/dingue.svg">
          <defs>
            <linearGradient id="dengueGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#1d6a96;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#4a98c9;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#e67e22;stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect width="100%" height="100%" fill="url(#dengueGradientPopup)"/>
          <circle cx="400" cy="125" r="40" fill="#27ae60" opacity="0.6"/>
          <text x="80" y="180" font-family="Arial" font-size="48" fill="white" font-weight="bold">DENGUE - PRÉVENTION</text>
        </img>

      `,
    },

    prep: {
      id: 'prep',
      title: 'PrEP VIH - Prophylaxie Pré-Exposition',
      author: 'Dr. Kokou Géoffroy DISSE',
      date: '17 Jui',
      tags: ['AMOUR ET SEXO', 'VIRUS ET ÉPIDÉMIES'],
      content: `
        <div class="popup-section">
          <h3>Qu'est-ce que la PrEP ?</h3>
          <p class="popup-text">La PrEP (Prophylaxie Pré-Exposition) est un traitement préventif qui consiste à prendre quotidiennement un médicament antirétroviral pour réduire le risque d'infection par le VIH.</p>
        </div>
        
        <div class="popup-section">
          <h3>Efficacité</h3>
          <p class="popup-text">Lorsqu'elle est prise correctement et régulièrement, la PrEP peut réduire le risque d'infection par le VIH de plus de 90% lors de rapports sexuels et de plus de 70% chez les utilisateurs de drogues injectables.</p>
        </div>
        
        <div class="popup-section">
          <h3>Pour qui ?</h3>
          <p class="popup-text">La PrEP est recommandée pour les personnes à haut risque d'exposition au VIH, notamment les partenaires de personnes séropositives, les personnes ayant des partenaires multiples, ou les utilisateurs de drogues injectables.</p>
        </div>
      `,
      headerImage: `
    
       <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/PrEP.svg">
          <defs>
            <linearGradient id="prepGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#4a90e2;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#50e3c2;stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect width="100%" height="100%" fill="url(#prepGradientPopup)"/>
          <text x="280" y="120" font-family="Arial" font-size="80" fill="#ff5252" font-weight="bold">PrEP</text>
          <text x="100" y="180" font-family="Arial" font-size="32" fill="#333" font-weight="bold">Prévention VIH</text>
        </img>
    

      `,
    },

    glycemie: {
      id: 'glycemie',
      title: '8 solutions naturelles pour faire baisser votre glycémie !',
      author: 'Dr.BABA',
      date: '16 Jan',
      tags: ['BIEN-ÊTRE', 'NUTRITION', 'SANTÉ'],
      content: `
        <div class="popup-section">
          <h3>1. L'exercice régulier</h3>
          <p class="popup-text">L'activité physique aide les muscles à utiliser le glucose, réduisant naturellement la glycémie. 30 minutes d'exercice par jour peuvent faire une grande différence.</p>
        </div>
        
        <div class="popup-section">
          <h3>2. Contrôler les portions</h3>
          <p class="popup-text">Réduire la taille des portions aide à maintenir un taux de glucose stable. Privilégiez des repas plus petits et plus fréquents.</p>
        </div>
        
        <div class="popup-section">
          <h3>3. Augmenter la consommation de fibres</h3>
          <p class="popup-text">Les aliments riches en fibres ralentissent l'absorption du sucre et améliorent la glycémie. Optez pour les légumes, fruits et céréales complètes.</p>
        </div>
        
        <div class="popup-section">
          <h3>4. Boire suffisamment d'eau</h3>
          <p class="popup-text">Une bonne hydratation aide les reins à éliminer l'excès de glucose par l'urine et peut prévenir la déshydratation liée à l'hyperglycémie.</p>
        </div>
      `,
      headerImage: `
  
            <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/Glycemie.svg">
              <defs>
                <linearGradient id="glycemieGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:#ffe082;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#ffb74d;stop-opacity:1" />
                </linearGradient>
              </defs>
              <rect width="100%" height="100%" fill="url(#glycemieGradientPopup)"/>
              <rect x="150" y="30" width="80" height="120" rx="5" fill="#e0e0e0"/>
              <rect x="160" y="40" width="60" height="40" rx="2" fill="#f0f0f0"/>
              <rect x="190" y="10" width="5" height="30" fill="#ffeb3b"/>
              <ellipse cx="150" cy="80" rx="10" ry="15" fill="#a1887f"/>
              <circle cx="150" cy="80" r="2" fill="#c62828"/>
            </img>
       

      `,
    },

    immunite: {
      id: 'immunite',
      title: "Alimentation saine pour renforcer l'immunité",
      author: 'Medico',
      date: '15 Jan',
      tags: ['NUTRITION', 'BIEN-ÊTRE'],
      content: `
        <div class="popup-section">
          <h3>Les super-aliments pour l'immunité</h3>
          <p class="popup-text">Certains aliments sont particulièrement bénéfiques pour renforcer le système immunitaire : agrumes riches en vitamine C, épinards, brocolis, ail, gingembre, et yaourt probiotique.</p>
        </div>
        
        <div class="popup-section">
          <h3>Vitamines essentielles</h3>
          <p class="popup-text">La vitamine C, la vitamine D, le zinc et les antioxydants jouent un rôle crucial dans le maintien d'un système immunitaire fort. Une alimentation variée et colorée est la clé.</p>
        </div>
        
        <div class="popup-section">
          <h3>Habitudes alimentaires saines</h3>
          <p class="popup-text">Adopter une alimentation équilibrée, limiter les aliments transformés, rester hydraté et maintenir un poids santé contribuent tous à un système immunitaire robuste.</p>
        </div>
      `,
      headerImage: `
        <div class="article-image">
          <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/immunité.svg">
            <defs>
              <linearGradient id="immuniteGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#81c784;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#4caf50;stop-opacity:1" />
              </linearGradient>
            </defs>
            <rect width="100%" height="100%" fill="url(#immuniteGradientPopup)"/>
            <circle cx="200" cy="100" r="40" fill="#e57373"/>
            <circle cx="320" cy="120" r="35" fill="#ffa726"/>
            <text x="100" y="60" font-family="Arial" font-size="36" fill="#333" font-weight="bold">Immunité Forte</text>
          </img>
        </div>

      `,
    },

    moustiques: {
      id: 'moustiques',
      title: 'Prévention contre les moustiques porteurs de maladies',
      author: 'Dr. Kokou Géoffroy DISSE',
      date: '14 Jui',
      tags: ['VIRUS ET ÉPIDÉMIES', 'SANTÉ'],
      content: `
        <div class="popup-section">
          <h3>Maladies transmises par les moustiques</h3>
          <p class="popup-text">Les moustiques peuvent transmettre de nombreuses maladies graves : paludisme, dengue, chikungunya, Zika, fièvre jaune. La prévention est essentielle.</p>
        </div>
        
        <div class="popup-section">
          <h3>Méthodes de protection</h3>
          <p class="popup-text">Utilisez des répulsifs contenant du DEET, portez des vêtements longs et clairs, dormez sous une moustiquaire, et éliminez les eaux stagnantes autour de votre domicile.</p>
        </div>
        
        <div class="popup-section">
          <h3>Contrôle environnemental</h3>
          <p class="popup-text">Videz régulièrement les récipients d'eau, entretenez les gouttières, couvrez les réservoirs d'eau et utilisez des larvicides si nécessaire.</p>
        </div>
      `,
      headerImage: `
        <div class="article-image">
          <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/moustique.svg">
            <defs>
              <linearGradient id="moustiqueGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#4fc3f7;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#0288d1;stop-opacity:1" />
              </linearGradient>
            </defs>
            <rect width="100%" height="100%" fill="url(#moustiqueGradientPopup)"/>
            <text x="100" y="60" font-family="Arial" font-size="36" fill="#333" font-weight="bold">Protection Moustiques</text>
            <circle cx="600" cy="150" r="40" fill="#ff8a80" opacity="0.8"/>
            <text x="560" y="158" font-family="Arial" font-size="20" fill="#e53935" font-weight="bold">DANGER</text>
          </img>
        </div>

      `,
    },

    fievre: {
      id: 'fievre',
      title: "Comment gérer la fièvre chez l'enfant",
      author: 'Mr.Kokou',
      date: '12 Jan',
      tags: ['PÉDIATRIE / PARENTALITÉ', 'SANTÉ'],
      content: `
        <div class="popup-section">
          <h3>Quand s'inquiéter ?</h3>
          <p class="popup-text">Consultez immédiatement si la température dépasse 40°C, si l'enfant a moins de 3 mois avec une fièvre de 38°C, ou s'il présente des signes de déshydratation ou de détresse respiratoire.</p>
        </div>
        
        <div class="popup-section">
          <h3>Mesures de confort</h3>
          <p class="popup-text">Maintenez l'enfant hydraté, habillez-le légèrement, aérez la pièce, et utilisez des compresses tièdes. Évitez les bains froids qui peuvent provoquer des frissons.</p>
        </div>
        
        <div class="popup-section">
          <h3>Médicaments</h3>
          <p class="popup-text">Le paracétamol ou l'ibuprofène peuvent être utilisés selon l'âge et le poids de l'enfant. Respectez toujours les doses prescrites et consultez un professionnel de santé.</p>
        </div>
      `,
      headerImage: `
        <div class="article-image">
          <img width="100%" height="100%" viewBox="0 0 800 250" src="assets/img/fievre.svg">
            <defs>
              <linearGradient id="fievreGradientPopup" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#ff8a65;stop-opacity:1" />
                <stop offset="50%" style="stop-color:#ff7043;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#d84315;stop-opacity:1" />
              </linearGradient>
            </defs>
            <rect width="100%" height="100%" fill="url(#fievreGradientPopup)"/>
            <circle cx="400" cy="125" r="50" fill="#f44336" opacity="0.6"/>
            <text x="250" y="140" font-family="Arial" font-size="48" fill="white" font-weight="bold">FIEVRE</text>
            <text x="200" y="190" font-family="Arial" font-size="24" fill="white" font-weight="bold">Prévention & Soins</text>
          </img>
        </div>

      `,
    },
  };

  constructor() {}

  openPopup(articleId: string): void {
    const article = this.articles[articleId];
    if (article) {
      this.selectedArticle = article;
      this.isPopupOpen = true;
      document.body.style.overflow = 'hidden';
    }
  }

  closePopup(): void {
    this.isPopupOpen = false;
    this.selectedArticle = null;
    document.body.style.overflow = 'auto';
  }

  closePopupOnOverlay(event: Event): void {
    if (event.target === event.currentTarget) {
      this.closePopup();
    }
  }

  saveArticle(articleId: string, event: Event): void {
    event.stopPropagation();
    console.log(`Article ${articleId} sauvegardé!`);
    // Ici vous pouvez ajouter la logique de sauvegarde
    alert('Article enregistré avec succès!');
  }

  getTagClass(tag: string): string {
    if (tag.includes('VIRUS') || tag.includes('ÉPIDÉMIES')) {
      return 'tag-yellow';
    } else if (tag.includes('BIEN-ÊTRE')) {
      return 'tag-green';
    } else if (tag.includes('NUTRITION')) {
      return 'tag-orange';
    } else if (
      tag.includes('SANTÉ') ||
      tag.includes('AMOUR') ||
      tag.includes('PÉDIATRIE')
    ) {
      return 'tag-pink';
    }
    return '';
  }

  // Méthode pour gérer la touche Escape
  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape' && this.isPopupOpen) {
      this.closePopup();
    }
  }
}
