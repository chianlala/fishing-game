
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class peopleUpdate1 : MonoBehaviour
    {  
        public Text txtCount;
        private int nMax = 3000;
        private int nMin = 5000;
        void Awake()
        {
            txtCount = this.transform.GetComponent<Text>();
            numP = Random.Range(nMin, nMax);
            txtCount.text = numP.ToString() + "人在线";
        }
        long numP = 0;
        float time = 0f;
        float targettime = 2f;
        void Update()
        {
            time += Time.deltaTime;
            if (time > targettime)
            {
                time = 0;
                targettime = Random.Range(1f, 5f);
                if (numP < nMin)
                {
                    numP += Random.Range(0, 8);
                }
                else if (numP > nMax)
                {
                    numP += Random.Range(-8, 0);
                }
                else
                {
                    numP += Random.Range(-8, 8);
                }

                txtCount.text = numP.ToString() + "人在线";
            }

        }
    }
}