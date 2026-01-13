using CoreGame;
using DG.Tweening;
using Game.UI;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class KillBoss : MonoBehaviour
{
    public Image Sp_fish;
    public Text txt_bei;
    public Text txt_gold;
    public Text txt_hedan;

    public Transform bang_hedanParent;
    public Transform bang_hedan;
    public Transform bang_hedanbang;
    
  
    public Text txt_hedanbang;
    // Start is called before the first frame update
    void Start()
    {
       
    }
    private void OnEnable()
    {
        //InitThis(15, 5000, 158464614, 50, 60);
        txt_bei.text = "";
        txt_gold.text = "";
        txt_hedan.text = "";
        txt_hedanbang.text = "";

        txt_gold.gameObject.SetActive(false);
        txt_hedan.gameObject.SetActive(false);
        txt_hedanbang.gameObject.SetActive(false);
        bang_hedanbang.gameObject.SetActive(false);
        bang_hedan.gameObject.SetActive(false);
        bang_hedanParent.gameObject.SetActive(false);
    }

    public void InitThis(long fishID, long bei, long gold, long hedan,long hedanbang)  
    {
     
        Sp_fish.sprite = Resources.Load<Sprite>("fishIco/"+common4.GetFishModleID(fishID));
        //txt_bei.text=bei.ToString();
        //txt_gold.text = gold.ToString();
        //txt_hedan.text = hedan.ToString();
        //txt_hedanbang.text = hedanbang.ToString();
        StartCoroutine(StartGunDong(fishID, bei, gold, hedan, hedanbang));
    } 
    IEnumerator StartGunDong(long fishID, long bei, long gold, long hedan, long hedanbang) {

        long M = bei / 20;
        for (int i = 0; i < 20; i++) 
        {
            if (bei - M * (20 - i) < 0)
            {
                txt_bei.text = "0";
            }
            else
            {
               
                txt_bei.text =  (bei - M * (20-i) ).ToString();
            }
            
            yield return new WaitForSeconds(0.1f);
        }
        txt_bei.text = bei.ToString();

        yield return new  WaitForSeconds(1f) ;

        txt_gold.text = string.Format("{0:N0}", gold); //gold.ToString();

        txt_gold.transform.localScale = Vector3.zero;
        txt_gold.transform.DOScale(1, 0.2f);

        txt_gold.gameObject.SetActive(true);

      //  yield return new WaitForSeconds(1f);

        if (hedan > 0)
        {
            yield return new WaitForSeconds(1f);
            bang_hedanParent.gameObject.SetActive(true);
            txt_hedan.text = hedan.ToString();
            txt_hedan.transform.localScale = Vector3.zero;
            txt_hedan.transform.DOScale(1, 0.2f);
            txt_hedan.gameObject.SetActive(true);
            bang_hedan.gameObject.SetActive(true);
        }
        else
        {
            txt_hedan.text = "0";
            txt_hedan.gameObject.SetActive(false);    
            bang_hedan.gameObject.SetActive(false);
        }
     

        if (hedanbang>0)
        {
            yield return new WaitForSeconds(1f);
            bang_hedanParent.gameObject.SetActive(true);
            txt_hedanbang.text = hedanbang.ToString();

            txt_hedanbang.transform.localScale = Vector3.zero;
            txt_hedanbang.transform.DOScale(1, 0.2f);

            txt_hedanbang.gameObject.SetActive(true);
            bang_hedanbang.gameObject.SetActive(true);
        
        }
        else
        {
            txt_hedanbang.text = "0";
            txt_hedanbang.gameObject.SetActive(false);
            bang_hedanbang.gameObject.SetActive(false);
        }

        if (hedan<=0&& hedanbang<=0)
        {
            bang_hedanParent.gameObject.SetActive(false);
        }
        yield return new WaitForSeconds(3f);

        this.gameObject.SetActive(false);
    }
}
