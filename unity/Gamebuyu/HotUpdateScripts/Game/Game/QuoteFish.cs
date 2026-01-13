using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;

namespace Game.UI
{
    public class QuoteFish : MonoBehaviour
    {
        public Transform transQuoteParent;
        public fish yinyongfish; 
        void Awake()
        {
            transQuoteParent= FindParent(this.transform);
            if (transQuoteParent==null)
            {
                Debug.LogError("transQuoteParent" + this.transform.name);
            }
            yinyongfish = transQuoteParent.GetComponent<fish>();
            if (yinyongfish != null)
            {
                if (this.transform.name== "hittrigger")
                {
                    yinyongfish.traCollider = this.transform;
                }
                //Debug.Log("yinyongfish" + yinyongfish.fishState.id);
            }
            else
            {
                Debug.LogError("yinyongfish" + this.transform.parent);
                Debug.LogError("yinyongfish");
            }
        }
        Transform FindParent(Transform item) {
         
            if (item.name== "FishView")
            {
              
                return item.parent;
            }
            else
            {
                if (item.parent == null)
                {
                    Debug.LogError("yinyongfish " + this.transform.name);
                    Debug.LogError("yinyongfish " + item.name);
                }
                return FindParent(item.parent);
            }
        }
    }
}
