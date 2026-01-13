using UnityEngine;
using System.Collections;

public class ParticleAutoDestruction : MonoBehaviour
{
    private ParticleSystem[] particleSystems;

    void Start()
    {
        particleSystems = GetComponentsInChildren<ParticleSystem>();
    }

    void Update()
    {
        bool allStopped = true;
        for (int i = 0; i < particleSystems.Length; i++)
        {
            ParticleSystem ps = particleSystems[i];
            if (!ps.isStopped)
            {
                allStopped = false;
            }
        }

        if (allStopped)
            GameObject.Destroy(gameObject);
    }
}
